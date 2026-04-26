package cn.hhh.campusmind.chat.service;

import cn.hhh.campusmind.chat.domain.po.AiChatSession;
import cn.hhh.campusmind.chat.domain.vo.SessionVO;
import cn.hhh.campusmind.common.page.PageQuery;
import cn.hhh.campusmind.common.page.PageResult;
import cn.hhh.campusmind.common.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * AI会话表：按用户维度管理多会话 服务类
 * </p>
 *
 * @author thyme
 * @since 2026-04-24
 */
public interface IAiChatSessionService extends IService<AiChatSession> {

    Result<Void> createSession();

    Result<Void> deleteSession(Long sessionId);

    Result<PageResult<SessionVO>> getSessionList(PageQuery pageQuery);

    Result<Void> renameSession(Long sessionId, String title);
}
