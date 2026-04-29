package cn.hhh.campusmind.chat.domain.po;

import cn.hhh.campusmind.chat.domain.enums.SessionStatusEnum;
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
 * AI会话表：按用户维度管理多会话
 * </p>
 *
 * @author thyme
 * @since 2026-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_chat_session")
public class AiChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID（关联sys_user.id）
     */
    private Long userId;

    /**
     * 会话类型：chat/plan/quiz/profile_update等
     */
    private String sessionType;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 会话状态：1进行中，2已归档，0禁用
     */
    private SessionStatusEnum status;

    /**
     * 会话上下文摘要（可用于快速补全上下文）
     */
    private String contextSummary;

    /**
     * 消息总数
     */
    private Integer messageCount;

    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageAt;

    /**
     * 会话名称（用于显示在前端）
     */
    private String ext;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
