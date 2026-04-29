package cn.hhh.campusmind.common.constants;

import lombok.Data;

@Data
public class RabbitMqConstants {
    /** 学生画像更新队列 */
    public static final String STUDENT_PROFILE_UPDATE_QUEUE = "student_profile_update_queue";
    /** 学生画像更新路由键 */
    public static final String STUDENT_PROFILE_UPDATE_ROUTING_KEY = "student_profile_update_routing_key";
    /** 学生画像更新交换机 */
    public static final String STUDENT_PROFILE_UPDATE_EXCHANGE = "student_profile_update_exchange";


    /** 生成会话标题路由键 */
    public static final String GENERATE_SESSION_TITLE_ROUTING_KEY = "generate_session_title_routingA_key";
    /** 生成会话标题交换机 */
    public static final String GENERATE_SESSION_TITLE_EXCHANGE = "generate_session_title_exchange";
    /** 生成会话标题队列 */
    public static final String GENERATE_SESSION_TITLE_QUEUE = "generate_session_title_queue";

    /** ai交互保存消息队列 */
    public static final String AI_INTERACTION_SAVE_MESSAGE_QUEUE = "ai_interaction_save_message_queue";
    /** ai交互保存消息路由键 */
    public static final String AI_INTERACTION_SAVE_MESSAGE_ROUTING_KEY = "ai_interaction_save_message_routing_key";
    /** ai交互保存消息交换机 */
    public static final String AI_INTERACTION_SAVE_MESSAGE_EXCHANGE = "ai_interaction_save_message_exchange";

    /** 知识库文档解析队列 */
    public static final String KB_DOCUMENT_PARSE_QUEUE = "kb_document_parse_queue";
    /** 知识库文档解析路由键 */
    public static final String KB_DOCUMENT_PARSE_ROUTING_KEY = "kb_document_parse_routing_key";
    /** 知识库文档解析交换机 */
    public static final String KB_DOCUMENT_PARSE_EXCHANGE = "kb_document_parse_exchange";
}
