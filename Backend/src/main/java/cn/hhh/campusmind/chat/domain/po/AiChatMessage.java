package cn.hhh.campusmind.chat.domain.po;

import cn.hhh.campusmind.chat.domain.enums.RoleEnum;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * AI消息表：记录会话内每一轮消息
 * </p>
 *
 * @author thyme
 * @since 2026-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_chat_message")
public class AiChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话唯一标识（关联ai_chat_session.session_id）
     */
    private Long sessionId;

    /**
     * 用户ID（冗余便于检索）
     */
    private Long userId;

    /**
     * 消息角色：user/assistant/system/tool
     */
    private RoleEnum role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 内容类型：text/json/markdown
     */
    private String contentType;

    /**
     * 本次请求输入token数
     */
    private Integer tokenInput;

    /**
     * 本次请求输出token数
     */
    private Integer tokenOutput;

    /**
     * 本次模型耗时毫秒
     */
    private Integer latencyMs;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 请求追踪ID
     */
    private String traceId;

    /**
     * 扩展字段JSON
     */
    private String meta;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


}
