package cn.hhh.campusmind.chat.controller;


import cn.hhh.campusmind.chat.domain.dto.SendMessageDTO;
import cn.hhh.campusmind.chat.domain.vo.ChatMessageVO;
import cn.hhh.campusmind.chat.service.IAiChatMessageService;
import cn.hhh.campusmind.common.page.PageQuery;
import cn.hhh.campusmind.common.page.PageResult;
import cn.hhh.campusmind.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * <p>
 * AI消息表：记录会话内每一轮消息 前端控制器
 * </p>
 *
 * @author thyme
 * @since 2026-04-24
 */
@RestController
@RequestMapping("/ai/chat/message")
@RequiredArgsConstructor
@Tag(name = "ai交互模块")
public class AiChatMessageController {

    private final IAiChatMessageService aiChatMessageService;

    /**
     * 获取会话内所有消息
     * @param sessionId 会话ID
     * @return
     */
    @Operation(summary = "获取会话内所有消息")
    @GetMapping("/list/{sessionId}")
    public Result<PageResult<ChatMessageVO>> listMessages(@PathVariable Long sessionId, @ModelAttribute PageQuery pageQuery) {
        return aiChatMessageService.getMessages(sessionId, pageQuery);
    }

    /**
     * 与前端创建sse连接,实现流式输出,结束后关闭连接,并异步保存消息
     * @param sendMessageDTO
     * @return
     */
    @Operation(summary = "聊天流")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@ModelAttribute SendMessageDTO sendMessageDTO) {
        return aiChatMessageService.chatStream(sendMessageDTO);
    }
}
