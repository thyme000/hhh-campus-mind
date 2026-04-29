package cn.hhh.campusmind.user.service.impl;

import cn.hhh.campusmind.user.domain.po.SysUser;
import cn.hhh.campusmind.user.mapper.SysUserMapper;
import cn.hhh.campusmind.user.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表（学生/教师/管理员）；登录鉴权主体。 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-19
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
