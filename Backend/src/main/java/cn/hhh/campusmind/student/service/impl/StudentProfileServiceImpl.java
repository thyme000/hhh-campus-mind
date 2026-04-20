package cn.hhh.campusmind.student.service.impl;

import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.common.utils.UserContext;
import cn.hhh.campusmind.student.domain.dto.EventDTO;
import cn.hhh.campusmind.student.domain.dto.FirstStudentProfileDTO;
import cn.hhh.campusmind.student.domain.enums.ResourcesTypeEnum;
import cn.hhh.campusmind.student.domain.po.StudentProfile;
import cn.hhh.campusmind.student.mapper.StudentProfileMapper;
import cn.hhh.campusmind.student.service.IStudentProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class StudentProfileServiceImpl extends ServiceImpl<StudentProfileMapper, StudentProfile> implements IStudentProfileService {

    private static final Integer RESOURCE_PREF = 75;

    @Override
    public Result<Void> firstCollect(FirstStudentProfileDTO firstStudentProfileDTO) {
        Long userId = UserContext.getUserId();
        // 检查用户是否已存在画像
        StudentProfile studentProfile = baseMapper.selectById(userId);
        if (studentProfile != null) {
            return Result.fail("该学生已存在画像");
        }
        // 新增初始画像
        StudentProfile profile = new StudentProfile();
        profile.setUserId(userId);
        profile.setKnowledgeLevel(firstStudentProfileDTO.getKnowledgeLevel());
        profile.setGoalText(firstStudentProfileDTO.getGoalText());
        ResourcesTypeEnum resourcePref = firstStudentProfileDTO.getResourcePref();
        switch (resourcePref) {
            case DOC -> profile.setResourcePrefDoc(RESOURCE_PREF);
            case VIDEO -> profile.setResourcePrefVideo(RESOURCE_PREF);
            case CODE -> profile.setResourcePrefCode(RESOURCE_PREF);
            case MINDMAP -> profile.setResourcePrefMindmap(RESOURCE_PREF);
            default -> log.warn("错误的资源偏好：{}", resourcePref.getTypeName());
        }
        profile.setPreferredTimeSlots(firstStudentProfileDTO.getPreferredTimeSlots());
        baseMapper.insert(profile);
        return Result.success();
    }

    // TODO
    @Override
    public Result<Void> triggerUpdateEvent(EventDTO eventDTO) {
        Long userId = UserContext.getUserId();
        // 发送RabbitMQ事件给ai服务
        return null;
    }
}
