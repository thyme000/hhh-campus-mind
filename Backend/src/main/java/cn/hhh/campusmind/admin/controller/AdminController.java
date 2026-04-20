package cn.hhh.campusmind.admin.controller;

import cn.hhh.campusmind.admin.domain.dto.UserDTO;
import cn.hhh.campusmind.admin.service.IAdminService;
import cn.hhh.campusmind.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/admin")
@Tag(name = "管理模块")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    /**
     * 根据文件批量导入学生账号 TODO 暂时只支持cvs的固定格式
     * @param file
     * @return
     */
    @Operation(summary = "批量导入学生账号")
    @PostMapping("/import/students")
    public Result<String> importStudents(@RequestPart("file") MultipartFile file) {
        return adminService.importStudents(file);
    }

    /**
     * 根据文件批量导入教师账号 TODO 暂时只支持cvs的固定格式
     * @param file
     * @return
     */
    @Operation(summary = "根据文件批量导入教师账号")
    @PostMapping("/import/teachers")
    public Result<String> importTeachers(@RequestPart("file") MultipartFile file) {
        return adminService.importTeachers(file);
    }

    /**
     * 单个导入学生账号
     * @param userDTO
     * @return
     */
    @Operation(summary = "单个导入学生账号")
    @PostMapping("/import/student")
    public Result<String> importStudent(@RequestBody UserDTO userDTO) {
        return adminService.importStudent(userDTO);
    }

    /**
     * 单个导入教师账号
     * @param userDTO
     * @return
     */
    @Operation(summary = "单个导入教师账号")
    @PostMapping("/import/teacher")
    public Result<String> importTeacher(@RequestBody UserDTO userDTO) {
        return adminService.importTeacher(userDTO);
    }
}
