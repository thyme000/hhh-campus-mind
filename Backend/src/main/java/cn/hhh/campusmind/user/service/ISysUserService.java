package cn.hhh.campusmind.user.service;

import cn.hhh.campusmind.user.domain.po.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * <p>
 * 系统用户表（学生/教师/管理员）；登录鉴权主体。 服务类
 * </p>
 *
 * @author thyme
 * @since 2026-04-19
 */
public interface ISysUserService extends IService<SysUser> {

    Map<String, Object> uploadAvatar(String userId, MultipartFile file);

    Map<String, Object> getAvatar(String userId);
}
