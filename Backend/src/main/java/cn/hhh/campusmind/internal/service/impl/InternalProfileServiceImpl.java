package cn.hhh.campusmind.internal.service.impl;

import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.internal.domain.dto.AiProfileResultDTO;
import cn.hhh.campusmind.profile.domain.enums.SnapshotStatusEnum;
import cn.hhh.campusmind.profile.domain.enums.TriggerTypeEnum;
import cn.hhh.campusmind.profile.domain.po.ProfileSnapshot;
import cn.hhh.campusmind.profile.domain.po.StudentProfile;
import cn.hhh.campusmind.profile.domain.vo.Delta;
import cn.hhh.campusmind.internal.service.IInternalProfileService;
import cn.hhh.campusmind.profile.service.IProfileSnapshotService;
import cn.hhh.campusmind.profile.service.IStudentProfileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static cn.hhh.campusmind.common.utils.JsonUtil.toJsonArray;
import static cn.hhh.campusmind.common.utils.JsonUtil.toJsonObject;

@Service
@RequiredArgsConstructor
public class InternalProfileServiceImpl implements IInternalProfileService {

    private final IProfileSnapshotService profileSnapshotService;
    private final IStudentProfileService studentProfileService;

    @Value("${INTERNAL_API_TOKEN:}")
    private String internalApiToken;

    @Override
    public Result<Void> aiResult(String token, AiProfileResultDTO dto) {
        if (StringUtils.hasText(internalApiToken) && !internalApiToken.equals(token)) {
            return Result.fail("内部接口token无效");
        }

        Long userId = dto.getUserId();
        Long version = dto.getProfileVersion() == null ? null : dto.getProfileVersion().longValue();
        TriggerTypeEnum triggerType = parseTriggerType(dto.getEventType());
        LocalDateTime now = LocalDateTime.now();

        // 新增或更新画像快照
        ProfileSnapshot snapshot = null;
        if (version != null) {
            // 说明是更新画像快照
            snapshot = profileSnapshotService.getOne(
                    new LambdaQueryWrapper<ProfileSnapshot>()
                            .eq(ProfileSnapshot::getUserId, userId)
                            .eq(ProfileSnapshot::getProfileVersion, version)
                            .eq(ProfileSnapshot::getTriggerType, triggerType)
                            .orderByDesc(ProfileSnapshot::getCreatedAt)
                            .last("limit 1"),
                    false
            );
        }

        if (snapshot == null) {
            // 说明是新增画像快照
            snapshot = new ProfileSnapshot();
            snapshot.setUserId(userId);
            snapshot.setProfileVersion(version == null ? 0L : version);
            snapshot.setTriggerType(triggerType);
            snapshot.setTriggerRefId(dto.getRefId() == null ? "" : String.valueOf(dto.getRefId()));
            snapshot.setCreatedAt(now);
        }

        snapshot.setDelta(toDeltaJson(dto.getDelta()));
        snapshot.setEvidence(toJsonArray(dto.getEvidence()));
        snapshot.setConfidence(toJsonObject(dto.getConfidence()));
        if (Boolean.TRUE.equals(dto.getShouldUpdate())) {
            // ai建议更新画像，状态为已应用
            snapshot.setStatus(SnapshotStatusEnum.APPLIED);
        } else {
            // ai建议不更新画像，状态为已跳过
            snapshot.setStatus(SnapshotStatusEnum.SKIPPED);
        }
        snapshot.setReason(dto.getReason() == null ? "" : dto.getReason());

        if (snapshot.getId() == null) {
            profileSnapshotService.save(snapshot);
        } else {
            profileSnapshotService.updateById(snapshot);
        }

        StudentProfile profile = studentProfileService.getOne(
                new LambdaQueryWrapper<StudentProfile>()
                        .eq(StudentProfile::getUserId, userId)
                        .last("limit 1"),
                false
        );
        if (profile != null) {
            Delta delta = dto.getDelta();
            profile.setEvidence(snapshot.getEvidence());
            profile.setConfidence(snapshot.getConfidence());
            profile.setUpdatedAt(now);
            applyDeltaToProfile(delta, profile);
            studentProfileService.updateById(profile);
        }

        return Result.success();
    }


    private TriggerTypeEnum parseTriggerType(String raw) {
        if (!StringUtils.hasText(raw)) {
            return TriggerTypeEnum.MANUAL;
        }
        String v = raw.trim().toLowerCase();
        for (TriggerTypeEnum e : TriggerTypeEnum.values()) {
            if (e.getCode().equalsIgnoreCase(v) || e.name().equalsIgnoreCase(v)) {
                return e;
            }
        }
        return TriggerTypeEnum.MANUAL;
    }

    private String toDeltaJson(Delta value) {
        return new GsonBuilder().create().toJson(value);
    }

    public static void applyDeltaToProfile(Delta delta, StudentProfile profile) {
        if (delta == null || profile == null) {
            return;
        }

        profile.setKnowledgeLevel(
                pickIntOrKeep(profile.getKnowledgeLevel(), delta.getKnowledgeLevel())
        );
        profile.setCodingLevel(
                pickIntOrKeep(profile.getCodingLevel(), delta.getCodingLevel())
        );
        profile.setLearningSpeed(
                pickIntOrKeep(profile.getLearningSpeed(), delta.getLearningSpeed())
        );
        profile.setMotivationLevel(
                pickIntOrKeep(profile.getMotivationLevel(), delta.getMotivationLevel())
        );

        profile.setStudyFrequencyWeek(
                pickIntOrKeep(profile.getStudyFrequencyWeek(), delta.getStudyFrequencyWeek())
        );

        profile.setResourcePrefDoc(
                pickIntOrKeep(profile.getResourcePrefDoc(), delta.getResourcePrefDoc())
        );
        profile.setResourcePrefVideo(
                pickIntOrKeep(profile.getResourcePrefVideo(), delta.getResourcePrefVideo())
        );
        profile.setResourcePrefCode(
                pickIntOrKeep(profile.getResourcePrefCode(), delta.getResourcePrefCode())
        );
        profile.setResourcePrefMindmap(
                pickIntOrKeep(profile.getResourcePrefMindmap(), delta.getResourcePrefMindmap())
        );
        profile.setPreferredTimeSlots(
                delta.getPreferredTimeSlots() == null ? profile.getPreferredTimeSlots() : delta.getPreferredTimeSlots().get("to").toString()
        );
        profile.setMistakeTypeStats(
                delta.getMistakeTypeStats() == null ? profile.getMistakeTypeStats() : delta.getMistakeTypeStats().get("to").toString()
        );
        profile.setWeakKpIds(
                delta.getWeakKpIds() == null ? profile.getWeakKpIds() : delta.getWeakKpIds().get("to").toString()
        );
    }

    private static Integer pickIntOrKeep(Integer oldValue, Map<String, Object> change) {
        if (change == null) {
            return oldValue;
        }
        Object to = change.get("to");
        if (to == null) {
            return oldValue;
        }
        int v;
        try {
            v = Integer.parseInt(to.toString());
        } catch (Exception e) {
            return oldValue;
        }
        return v == 0 ? oldValue : v;
    }
}
