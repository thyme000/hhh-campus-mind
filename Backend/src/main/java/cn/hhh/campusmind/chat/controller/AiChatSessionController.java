package cn.hhh.campusmind.chat.controller;


import cn.hhh.campusmind.chat.domain.vo.SessionVO;
import cn.hhh.campusmind.chat.service.IAiChatSessionService;
import cn.hhh.campusmind.common.page.PageQuery;
import cn.hhh.campusmind.common.page.PageResult;
import cn.hhh.campusmind.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * AI会话表：按用户维度管理多会话 前端控制器
 * </p>
 *
 * @author thyme
 * @since 2026-04-24
 */
@RestController
@RequestMapping("/ai/chat/session")
@RequiredArgsConstructor
@Tag(name = "ai交互模块")
public class AiChatSessionController {

    private final IAiChatSessionService aiChatSessionService;

    /**
     * 创建会话
     * @return
     */
    @PutMapping("/create")
    @Operation(summary = "创建会话")
    public Result<Void> createSession() {
        return aiChatSessionService.createSession();
    }

    /**
     * 删除会话
     * @param sessionId
     * @return
     */
    @Operation(summary = "删除会话")
    @PutMapping("/delete/{sessionId}")
    public Result<Void> deleteSession(@PathVariable Long sessionId) {
        return aiChatSessionService.deleteSession(sessionId);
    }

    /**
     * 获取会话列表
     * @param pageQuery
     * @return
     */
    @Operation(summary = "获取会话列表")
    @GetMapping("/list")
    public Result<PageResult<SessionVO>> getSessionList(@ModelAttribute PageQuery pageQuery) {
        return aiChatSessionService.getSessionList(pageQuery);
    }

    /**
     * 重命名会话标题
     * @param sessionId
     * @return
     */
    @Operation(summary = "重命名会话标题")
    @PostMapping("/rename/{sessionId}")
    public Result<Void> renameSession(@PathVariable Long sessionId, @RequestBody String title) {
        return aiChatSessionService.renameSession(sessionId, title);
    }

}
