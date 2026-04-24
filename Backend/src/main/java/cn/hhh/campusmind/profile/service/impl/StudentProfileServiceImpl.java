package cn.hhh.campusmind.profile.service.impl;

import cn.hhh.campusmind.common.constants.RabbbitMqConstants;
import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.common.utils.UserContext;
import cn.hhh.campusmind.profile.domain.dto.EventDTO;
import cn.hhh.campusmind.profile.domain.dto.FirstStudentProfileDTO;
import cn.hhh.campusmind.profile.domain.enums.ResourcesTypeEnum;
import cn.hhh.campusmind.profile.domain.enums.SnapshotStatusEnum;
import cn.hhh.campusmind.profile.domain.enums.TriggerTypeEnum;
import cn.hhh.campusmind.profile.domain.messages.TriggerUpdateEventMessage;
import cn.hhh.campusmind.profile.domain.po.ProfileSnapshot;
import cn.hhh.campusmind.profile.domain.po.StudentProfile;
import cn.hhh.campusmind.profile.domain.vo.Delta;
import cn.hhh.campusmind.profile.domain.vo.ProfileForJson;
import cn.hhh.campusmind.profile.mapper.StudentProfileMapper;
import cn.hhh.campusmind.profile.service.IProfileSnapshotService;
import cn.hhh.campusmind.profile.service.IStudentProfileService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static cn.hhh.campusmind.common.utils.JsonUtil.normalizeJsonArray;

/**
 * <p>
 * 学生 画像当前汇总表：每个用户1行；用于个性化推荐、路径规划与展示。 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-19
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StudentProfileServiceImpl extends ServiceImpl<StudentProfileMapper, StudentProfile> implements IStudentProfileService {

    private static final Integer RESOURCE_PREF = 75;
    private static final Integer DEFAULT_SCORE = 50;
    private static final Integer DEFAULT_STUDY_FREQUENCY_WEEK = 3;

    private final IProfileSnapshotService profileSnapshotService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Result<Void> firstCollect(FirstStudentProfileDTO firstStudentProfileDTO) {
        Long userId = UserContext.getUserId();
        // 检查用户是否已存在画像
        StudentProfile studentProfile = baseMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getUserId, userId)
                .eq(StudentProfile::getIsDelete, 0));
        if (studentProfile != null) {
            return Result.fail("该学生已存在画像");
        }
        // 新增初始画像
        StudentProfile profile = new StudentProfile();
        profile.setUserId(userId);
        profile.setKnowledgeLevel(firstStudentProfileDTO.getKnowledgeLevel() == null ? DEFAULT_SCORE : firstStudentProfileDTO.getKnowledgeLevel());
        profile.setCodingLevel(DEFAULT_SCORE);
        profile.setLearningSpeed(DEFAULT_SCORE);
        profile.setMotivationLevel(DEFAULT_SCORE);
        profile.setGoalText(firstStudentProfileDTO.getGoalText() == null ? "" : firstStudentProfileDTO.getGoalText());
        ResourcesTypeEnum resourcePref = firstStudentProfileDTO.getResourcePref();
        if (resourcePref != null) {
            switch (resourcePref) {
                case DOC -> profile.setResourcePrefDoc(RESOURCE_PREF);
                case VIDEO -> profile.setResourcePrefVideo(RESOURCE_PREF);
                case CODE -> profile.setResourcePrefCode(RESOURCE_PREF);
                case MINDMAP -> profile.setResourcePrefMindmap(RESOURCE_PREF);
                default -> {
                    return Result.fail("资源偏好不能为空");
                }
            }
        }
        profile.setStudyFrequencyWeek(DEFAULT_STUDY_FREQUENCY_WEEK);
        profile.setPreferredTimeSlots(normalizeJsonArray(firstStudentProfileDTO.getPreferredTimeSlots()));
        baseMapper.insert(profile);
        // 新增初始画像快照
        ProfileSnapshot snapshot = new ProfileSnapshot();
        snapshot.setUserId(userId);
        snapshot.setProfileVersion(1L);
        snapshot.setTriggerType(TriggerTypeEnum.ONBOARDING);
        snapshot.setTriggerRefId("");
        snapshot.setProfile(buildProfileJson(profile));
        snapshot.setEvidence(profile.getEvidence());
        snapshot.setConfidence(profile.getConfidence());
        snapshot.setStatus(SnapshotStatusEnum.APPLIED);
        profileSnapshotService.save(snapshot);

        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> triggerUpdateEvent(EventDTO eventDTO) {
        Long userId = UserContext.getUserId();
        if (eventDTO == null || eventDTO.getEventType() == null) {
            return Result.fail("事件类型不能为空");
        }

        StudentProfile profile = baseMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getUserId, userId)
                .eq(StudentProfile::getIsDelete, 0));

        LocalDateTime now = LocalDateTime.now();
        if (profile == null) {
            // 画像不存在,重新构建
            profile = new StudentProfile();
            profile.setUserId(userId);
            profile.setKnowledgeLevel(DEFAULT_SCORE);
            profile.setCodingLevel(DEFAULT_SCORE);
            profile.setLearningSpeed(DEFAULT_SCORE);
            profile.setMotivationLevel(DEFAULT_SCORE);
            profile.setStudyFrequencyWeek(DEFAULT_STUDY_FREQUENCY_WEEK);
            profile.setProfileVersion(0L);
            profile.setIsDelete(0);
            profile.setCreatedAt(now);
            profile.setUpdatedAt(now);
            baseMapper.insert(profile);
        }
        // 更新学生画像
        Long oldVersion = profile.getProfileVersion() == null ? 0L : profile.getProfileVersion();
        Long newVersion = oldVersion + 1;

        Integer oldFreq = profile.getStudyFrequencyWeek() == null ? DEFAULT_STUDY_FREQUENCY_WEEK : profile.getStudyFrequencyWeek();
        Integer newFreq = Math.min(14, oldFreq + 1);
        profile.setStudyFrequencyWeek(newFreq);
        profile.setProfileVersion(newVersion);
        profile.setUpdatedAt(now);
        baseMapper.updateById(profile);

        // 新增画像快照
        ProfileSnapshot snapshot = new ProfileSnapshot();
        snapshot.setUserId(userId);
        snapshot.setProfileVersion(newVersion);
        snapshot.setTriggerType(eventDTO.getEventType());
        snapshot.setTriggerRefId(eventDTO.getRefId() == null ? "" : String.valueOf(eventDTO.getRefId()));
        snapshot.setDelta(buildDeltaJson(eventDTO.getEventType(), oldFreq, newFreq, oldVersion, newVersion));
        snapshot.setProfile(buildProfileJson(profile));
        snapshot.setEvidence(profile.getEvidence() == null ? "[]" : profile.getEvidence());
        snapshot.setConfidence(profile.getConfidence() == null ? "{}" : profile.getConfidence());
        snapshot.setStatus(SnapshotStatusEnum.APPLIED);
        snapshot.setReason("");
        snapshot.setCreatedAt(now);
        profileSnapshotService.save(snapshot);

        if (eventDTO.getEventType() == TriggerTypeEnum.QUIZ) {
            // TODO测验事件, 把用户做错的知识点和作对的知识点传递给ai服务, 让ai更好从不同角度分析
        }

        // TODO发送mq消息给ai服务,让ai判断是否需要更新学生画像
        TriggerUpdateEventMessage message = TriggerUpdateEventMessage.builder()
                .eventType(eventDTO.getEventType().getCode())
                .profileVersion(newVersion).occurredAt(now)
                .userId(userId).build();

        rabbitTemplate.convertAndSend(
                RabbbitMqConstants.STUDENT_PROFILE_UPDATE_EXCHANGE,
                RabbbitMqConstants.STUDENT_PROFILE_UPDATE_ROUTING_KEY,
                message
        );
        return Result.success();
    }

    private String buildProfileJson(StudentProfile profile) {
        ProfileForJson view = BeanUtil.toBean(profile, ProfileForJson.class);
        return new GsonBuilder().create().toJson(view);
    }

    private String buildDeltaJson(TriggerTypeEnum eventType, Integer oldFreq,
                                  Integer newFreq, Long oldVersion, Long newVersion) {
        Delta delta = new Delta();
        delta.setEventType(eventType.getCode());
        delta.setStudyFrequencyWeek(Map.of("from", oldFreq, "to", newFreq));
        delta.setProfileVersion(Map.of("from", oldVersion, "to", newVersion));
        return new GsonBuilder().create().toJson(delta);
    }
}
