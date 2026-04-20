package cn.hhh.campusmind.student.controller;


import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.student.domain.dto.EventDTO;
import cn.hhh.campusmind.student.domain.dto.FirstStudentProfileDTO;
import cn.hhh.campusmind.student.service.IStudentProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 学生画像当前汇总表：每个用户1行；用于个性化推荐、路径规划与展示。 前端控制器
 * </p>
 *
 * @author thyme
 * @since 2026-04-19
 */
@RestController
@RequestMapping("/student/profile")
@Tag(name = "学生画像模块")
@RequiredArgsConstructor
public class StudentProfileController {

    private final IStudentProfileService studentProfileService;

    /**
     * 首次收集该学生画像
     * @param firstStudentProfileDTO
     * @return
     */
    @Operation(summary = "首次收集该学生画像")
    @PutMapping("/collect")
    public Result<Void> firstCollect(@ModelAttribute FirstStudentProfileDTO firstStudentProfileDTO) {
        return studentProfileService.firstCollect(firstStudentProfileDTO);
    }


    /**
     * 触发学生画像更新事件
     * @param eventDTO
     * @return
     */
    @Operation(summary = "触发学生画像更新事件")
    @PostMapping("/event")
    public Result<Void> triggerUpdateEvent(@RequestBody EventDTO eventDTO) {
        return studentProfileService.triggerUpdateEvent(eventDTO);
    }
}
