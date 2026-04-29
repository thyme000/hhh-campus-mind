package cn.hhh.campusmind.admin.service.impl;

import cn.hhh.campusmind.admin.domain.dto.UserDTO;
import cn.hhh.campusmind.admin.service.IAdminService;
import cn.hhh.campusmind.department.domain.po.College;
import cn.hhh.campusmind.department.service.ICollegeService;
import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.common.utils.PasswordEncoder;
import cn.hhh.campusmind.common.utils.UserContext;
import cn.hhh.campusmind.user.domain.enums.StatusEnum;
import cn.hhh.campusmind.user.domain.enums.UserType;
import cn.hhh.campusmind.user.domain.po.SysUser;
import cn.hhh.campusmind.user.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {

    private final ISysUserService sysUserService;
    private final ICollegeService collegeService;

    private static final String DEFAULT_PASSWORD = "123456";


    @Override
    public Result<String> importStudents(MultipartFile file) {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null || !user.getRole().equals(UserType.ADMIN)) {
            return Result.fail("没有权限");
        }
        // 解析文件 -> 批量导入学生账号
        StringBuilder result = new StringBuilder();
        List<SysUser> users = getUsersFromFile(file, result, UserType.STUDENT);
        if (users == null) {
            return Result.fail(result.toString());
        }
        sysUserService.saveBatch(users);
        return Result.success(result.toString());
    }

    private List<SysUser> getUsersFromFile(MultipartFile file, StringBuilder result, UserType userType) {
        List<SysUser> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            // 跳过表头
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    log.warn("数据格式错误: {}", line);
                    result.append("数据格式错误: ").append(line).append("\n");
                    continue;
                }
                Long studentId = Long.parseLong(parts[0].trim());
                String username = parts[1].trim();
                String phone = parts[2].trim();
                Long collegeId = Long.parseLong(parts[3].trim());
                // 根据学院名称查找学院ID
                College college = collegeService.getOne(new LambdaQueryWrapper<College>()
                        .eq(College::getId, collegeId)
                        .eq(College::getIsDelete, 0));
                if (college == null) {
                    log.warn("学院不存在: {}", collegeId);
                    result.append("学院不存在: ").append(collegeId).append("\n");
                    continue;
                }
                // 检查账号是否已存在
                SysUser existingUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getId, studentId)
                        .eq(SysUser::getIsDelete, 0));
                if (existingUser != null) {
                    log.warn("账号已存在: {}", studentId);
                    result.append("账号已存在: ").append(studentId).append("\n");
                    continue;
                }
                SysUser student = getUser(studentId, username, userType, phone, collegeId);
                users.add(student);
            }
        } catch (IOException e) {
            log.error("文件处理失败", e);
            return null;
        }
        return users;
    }

    @Override
    public Result<String> importTeachers(MultipartFile file) {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null || !user.getRole().equals(UserType.ADMIN)) {
            return Result.fail("没有权限");
        }
        // 解析文件 -> 批量导入教师账号
        StringBuilder result = new StringBuilder();
        List<SysUser> users = getUsersFromFile(file, result, UserType.TEACHER);
        if (users == null) {
            return Result.fail(result.toString());
        }
        sysUserService.saveBatch(users);
        return Result.success(result.toString());
    }

    @Override
    public Result<String> importStudent(UserDTO userDTO) {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null || !user.getRole().equals(UserType.ADMIN)) {
            return Result.fail("没有权限");
        }
        SysUser u = getUser(userDTO.getUserId(), userDTO.getUsername(),
                UserType.STUDENT, userDTO.getPhone(), userDTO.getCollegeId());
        sysUserService.save(u);
        return Result.success("导入成功");
    }

    @Override
    public Result<String> importTeacher(UserDTO userDTO) {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null || !user.getRole().equals(UserType.ADMIN)) {
            return Result.fail("没有权限");
        }
        SysUser u = getUser(userDTO.getUserId(), userDTO.getUsername(),
                UserType.TEACHER, userDTO.getPhone(), userDTO.getCollegeId());
        sysUserService.save(u);
        return Result.success("导入成功");
    }

    private static SysUser getUser(Long studentId, String username, UserType userType,
                                   String phone, Long collegeId) {
        SysUser student = new SysUser();
        student.setId(studentId);
        student.setUsername(username);
        student.setPasswordHash(PasswordEncoder.encode(DEFAULT_PASSWORD));
        student.setRole(userType);
        student.setStatus(StatusEnum.ACTIVE);
        student.setPhone(phone);
        student.setCollegeId(collegeId);
        student.setIsDelete(0);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        return student;
    }
}
