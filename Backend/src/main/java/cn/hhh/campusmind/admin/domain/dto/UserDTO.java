package cn.hhh.campusmind.admin.domain.dto;

import lombok.Data;

@Data
public class UserDTO {
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 手机号 */
    private String phone;
    /** 学院id */
    private Long collegeId;
}
