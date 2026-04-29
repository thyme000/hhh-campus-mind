package cn.hhh.campusmind;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

// ===================== 队友原代码 完全不动 =====================
@SpringBootApplication
@MapperScan("cn.hhh.campusmind.*.mapper")
@RestController // 我只加了这一个注解，不影响任何功能
public class HhhCampusMindApplication {

    // 队友原main方法 完全不动
    public static void main(String[] args) {
        SpringApplication.run(HhhCampusMindApplication.class, args);
    }

// ===================== 下面全是你新增的头像功能 =====================
// 队友代码 一行没改 一行没删

    private final JdbcTemplate jdbcTemplate;
    public HhhCampusMindApplication(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 头像上传接口
    @PostMapping("/api/upload-avatar")
    public Map<String, Object> uploadAvatar(
            @RequestParam("user_id") String userId,
            @RequestParam("file") MultipartFile file) {

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".png") && !fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg"))) {
            return Map.of("code", -1, "msg", "只支持png/jpg/jpeg格式");
        }

        try {
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = "avatar_" + userId + "_" + UUID.randomUUID() + suffix;

            File dir = new File("src/main/resources/static/avatars");
            if (!dir.exists()) dir.mkdirs();
            File destFile = new File(dir, newFileName);
            file.transferTo(destFile);

            String avatarUrl = "/avatars/" + newFileName;
            jdbcTemplate.update(
                    "UPDATE student_profile SET avatar_url = ? WHERE user_id = ?",
                    avatarUrl, Integer.parseInt(userId)
            );

            return Map.of("code", 0, "msg", "上传成功", "avatar_url", avatarUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("code", -1, "msg", "上传失败");
        }
    }

    // 获取头像接口
    @GetMapping("/api/get-avatar")
    public Map<String, Object> getAvatar(@RequestParam String user_id) {
        String avatarUrl = jdbcTemplate.queryForObject(
                "SELECT avatar_url FROM student_profile WHERE user_id = ?",
                new Object[]{Integer.parseInt(user_id)},
                String.class
        );
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            avatarUrl = "/avatars/default.png";
        }
        return Map.of("avatar_url", avatarUrl);
    }

}