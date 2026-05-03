package cn.hhh.campusmind.user.service.impl;

import cn.hhh.campusmind.common.utils.AliOssUtil;
import cn.hhh.campusmind.user.domain.po.SysUser;
import cn.hhh.campusmind.user.mapper.SysUserMapper;
import cn.hhh.campusmind.user.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final SysUserMapper sysUserMapper;
    private final AliOssUtil aliOssUtil; // 注入OSS工具

    @Override
    public Map<String, Object> uploadAvatar(String userId, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".png") && !fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg"))) {
            return Map.of("code", -1, "msg", "只支持png/jpg/jpeg格式");
        }

        try {
            // 真正上传到阿里云OSS（使用你项目自带的工具）
            String avatarUrl = aliOssUtil.upload(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    Long.parseLong(userId),
                    "avatar"  // 业务前缀：头像
            );

            // 更新数据库
            SysUser user = new SysUser();
            user.setId(Long.parseLong(userId));
            user.setAvatarUrl(avatarUrl);
            sysUserMapper.updateById(user);

            return Map.of("code", 0, "msg", "上传成功", "avatar_url", avatarUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("code", -1, "msg", "上传失败");
        }
    }

    @Override
    public Map<String, Object> getAvatar(String user_id) {
        try {
            SysUser user = sysUserMapper.selectById(Long.parseLong(user_id));
            String avatarUrl = (user == null || user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty())
                    ? "/assets/default-avatar.png"
                    : user.getAvatarUrl();
            return Map.of("avatar_url", avatarUrl);
        } catch (Exception e) {
            return Map.of("avatar_url", "/assets/default-avatar.png");
        }
    }
}