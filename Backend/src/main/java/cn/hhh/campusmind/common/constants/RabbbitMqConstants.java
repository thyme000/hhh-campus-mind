package cn.hhh.campusmind.common.constants;

import lombok.Data;

@Data
public class RabbbitMqConstants {
    /** 学生画像更新队列 */
    public static final String STUDENT_PROFILE_UPDATE_QUEUE = "student_profile_update_queue";
    /** 学生画像更新路由键 */
    public static final String STUDENT_PROFILE_UPDATE_ROUTING_KEY = "student_profile_update_routing_key";
    /** 学生画像更新交换机 */
    public static final String STUDENT_PROFILE_UPDATE_EXCHANGE = "student_profile_update_exchange";
}
