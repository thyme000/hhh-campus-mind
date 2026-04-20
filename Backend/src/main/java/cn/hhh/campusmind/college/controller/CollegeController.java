package cn.hhh.campusmind.college.controller;



import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 学院字典表：用于规范学院名称，便于导入校验、下拉选择与统计。 前端控制器
 * </p>
 *
 * @author thyme
 * @since 2026-04-20
 */
@RestController
@RequestMapping("/college")
@Tag(name = "学院模块")
public class CollegeController {

}
