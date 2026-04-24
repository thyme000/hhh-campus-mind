package cn.hhh.campusmind.auth.domain.dto;

import cn.hhh.campusmind.user.domain.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDTO {

    /** 学号或工号 */
    @NotNull(message = "学号或工号不能为空")
    private Long userId;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 学院名称 */
    @NotBlank(message = "学院名称不能为空")
    private String collegeName;

    /** 用户类型 */
    @NotNull(message = "用户类型不能为空")
    private UserType userType;
}
