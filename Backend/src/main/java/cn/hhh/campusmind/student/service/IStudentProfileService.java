package cn.hhh.campusmind.student.service;

import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.student.domain.dto.EventDTO;
import cn.hhh.campusmind.student.domain.dto.FirstStudentProfileDTO;
import cn.hhh.campusmind.student.domain.po.StudentProfile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学生画像当前汇总表：每个用户1行；用于个性化推荐、路径规划与展示。 服务类
 * </p>
 *
 * @author thyme
 * @since 2026-04-19
 */
public interface IStudentProfileService extends IService<StudentProfile> {

    Result<Void> firstCollect(FirstStudentProfileDTO firstStudentProfileDTO);

    Result<Void> triggerUpdateEvent(EventDTO eventDTO);
}
