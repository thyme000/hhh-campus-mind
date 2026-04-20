package cn.hhh.campusmind.auth.domain.dto;

import cn.hhh.campusmind.user.domain.enums.UserType;
import lombok.Data;

@Data
public class LoginDTO {

    /** 学号或工号 */
    private Long userId;

    /** 密码 */
    private String password;

    /** 学院名称 */
    private String collegeName;

    /** 用户类型 */
    private UserType userType;
}
