package cn.hhh.campusmind.auth.service.impl;

import  cn.hhh.campusmind.auth.domain.dto.LoginDTO;
import cn.hhh.campusmind.auth.service.IAuthService;
import cn.hhh.campusmind.college.domain.po.College;
import cn.hhh.campusmind.college.service.ICollegeService;
import cn.hhh.campusmind.common.constants.RedisConstants;
import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.common.utils.PasswordEncoder;
import cn.hhh.campusmind.user.domain.enums.StatusEnum;
import cn.hhh.campusmind.user.domain.enums.UserType;
import cn.hhh.campusmind.user.domain.po.SysUser;
import cn.hhh.campusmind.user.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final ISysUserService sysUserService;
    private final StringRedisTemplate redisTemplate;
    private final ICollegeService collegeService;

    @Override
    public Result<String> login(LoginDTO loginDTO, HttpServletRequest request) {
        // 判断登录类型
        SysUser user = null;
        Long userId;
        try {
            userId = loginDTO.getUserId();
        } catch (Exception e) {
            return Result.fail("学号/工号格式错误");
        }
        College college = collegeService.lambdaQuery().eq(College::getName, loginDTO.getCollegeName()).one();
        if (loginDTO.getUserType().equals(UserType.STUDENT)) {
            if (college == null) {
                log.warn("学院不存在,学院名称{}", loginDTO.getCollegeName());
                return Result.fail("学院不存在");
            }
            // 学生登录
            user = sysUserService.lambdaQuery()
                    .eq(SysUser::getId, userId).eq(SysUser::getStatus, StatusEnum.ACTIVE)
                    .eq(SysUser::getCollegeId, college.getId())
                    .eq(SysUser::getRole, UserType.STUDENT).one();
        } else if (loginDTO.getUserType().equals(UserType.TEACHER)) {
            if (college == null) {
                log.warn("学院不存在,学院名称{}", loginDTO.getCollegeName());
                return Result.fail("学院不存在");
            }
            // 教师登录
            user = sysUserService.lambdaQuery()
                    .eq(SysUser::getId, userId).eq(SysUser::getStatus, StatusEnum.ACTIVE)
                    .eq(SysUser::getRole, UserType.TEACHER).one();
        } else {
            // 管理员登录
            user = sysUserService.lambdaQuery()
                    .eq(SysUser::getId, userId).eq(SysUser::getStatus, StatusEnum.ACTIVE)
                    .eq(SysUser::getRole, UserType.ADMIN).one();
        }
        if (user == null) {
            log.warn("用户不存在,用户类型{}", loginDTO.getUserType());
            return Result.fail("用户不存在");
        }
        // 判断密码是否正确
        if (!PasswordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            return Result.fail("密码错误");
        }
        // 生成 token，将 userId 关键字段以 字符串 形式存入 Redis
        String token = UUID.randomUUID().toString();
        String tokenKey = RedisConstants.USER_TOKEN + token;
        redisTemplate.opsForValue().set(tokenKey, String.valueOf(userId));
        redisTemplate.expire(tokenKey, RedisConstants.USER_TOKEN_EXPIRE, TimeUnit.HOURS);
        log.info("用户 [{}] 登录成功", userId);
        // 更新用户最后登录时间、最后登录IP、最后登录设备
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(resolveClientIp(request));
        sysUserService.updateById(user);
        return Result.success(token);
    }

    /**
     * 解析客户端IP
     * @param request
     * @return
     */
    private static String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) {
            return xri.trim();
        }
        return request.getRemoteAddr();
    }
}
