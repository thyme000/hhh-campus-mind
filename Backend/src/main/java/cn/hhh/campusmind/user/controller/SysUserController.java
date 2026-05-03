package cn.hhh.campusmind.user.controller;

import cn.hhh.campusmind.user.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService sysUserService;

    @PostMapping("/upload-avatar")
    public Map<String, Object> uploadAvatar(
            @RequestParam("user_id") String userId,
            @RequestParam("file") MultipartFile file) {
        return sysUserService.uploadAvatar(userId, file);
    }

    @GetMapping("/get-avatar")
    public Map<String, Object> getAvatar(@RequestParam String user_id) {
        return sysUserService.getAvatar(user_id);
    }
}