package cn.hhh.campusmind.chat.service;

import cn.hhh.campusmind.chat.domain.dto.SendMessageDTO;
import cn.hhh.campusmind.chat.domain.po.AiChatMessage;
import cn.hhh.campusmind.chat.domain.vo.ChatMessageVO;
import cn.hhh.campusmind.common.page.PageQuery;
import cn.hhh.campusmind.common.page.PageResult;
import cn.hhh.campusmind.common.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * <p>
 * AI消息表：记录会话内每一轮消息 服务类
 * </p>
 *
 * @author thyme
 * @since 2026-04-24
 */
public interface IAiChatMessageService extends IService<AiChatMessage> {

    Result<PageResult<ChatMessageVO>> getMessages(Long sessionId, PageQuery pageQuery);

    SseEmitter chatStream(SendMessageDTO sendMessageDTO);
}
