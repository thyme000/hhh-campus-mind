package cn.hhh.campusmind.admin.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {
    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;
    /** 手机号 */
    @NotBlank(message = "手机号不能为空")
    private String phone;
    /** 学院id */
    @NotNull(message = "学院id不能为空")
    private Long collegeId;
}
