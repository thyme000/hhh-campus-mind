package cn.hhh.campusmind.chat.service.impl;

import cn.hhh.campusmind.chat.domain.enums.SessionStatusEnum;
import cn.hhh.campusmind.chat.domain.po.AiChatMessage;
import cn.hhh.campusmind.chat.domain.po.AiChatSession;
import cn.hhh.campusmind.chat.domain.vo.SessionVO;
import cn.hhh.campusmind.chat.mapper.AiChatSessionMapper;
import cn.hhh.campusmind.chat.service.IAiChatMessageService;
import cn.hhh.campusmind.chat.service.IAiChatSessionService;
import cn.hhh.campusmind.common.page.PageQuery;
import cn.hhh.campusmind.common.page.PageResult;
import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.common.utils.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * AI会话表：按用户维度管理多会话 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-24
 */
@Service
@RequiredArgsConstructor
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSession> implements IAiChatSessionService {

    private final IAiChatMessageService aiChatMessageService;

    @Override
    public Result<Void> createSession() {
        AiChatSession aiChatSession = new AiChatSession();
        aiChatSession.setStatus(SessionStatusEnum.ACTIVE);
        aiChatSession.setUserId(UserContext.getUserId());
        save(aiChatSession);
        return Result.success();
    }

    @Transactional
    @Override
    public Result<Void> deleteSession(Long sessionId) {
        Long userId = UserContext.getUserId();
        // 判断是否是当前用户的会话
        AiChatSession aiChatSession = getOne(new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getUserId, userId)
                .eq(AiChatSession::getId, sessionId));
        if (aiChatSession == null) {
            return Result.fail("会话不存在");
        }
        // 删除会话
        lambdaUpdate()
                .eq(AiChatSession::getUserId, userId)
                .eq(AiChatSession::getId, sessionId)
                .set(AiChatSession::getIsDelete, 1)
                .update();
        // 删除会话下的所有消息
        aiChatMessageService.lambdaUpdate()
                .eq(AiChatMessage::getSessionId, sessionId)
                .eq(AiChatMessage::getUserId, userId)
                .set(AiChatMessage::getIsDelete, 1)
                .update();
        return Result.success();
    }

    @Override
    public Result<PageResult<SessionVO>> getSessionList(PageQuery pageQuery) {
        Long userId = UserContext.getUserId();
        Page<AiChatSession> page = lambdaQuery().eq(AiChatSession::getUserId, userId)
                .eq(AiChatSession::getStatus, SessionStatusEnum.ACTIVE)
                .eq(AiChatSession::getIsDelete, 0)
                .orderByDesc(AiChatSession::getCreatedAt)
                .page(pageQuery.toPage());
        if (page == null || page.getRecords() == null) {
            return Result.success(PageResult.of(0, 0, List.of()));
        }
        // 封po为vo
        List<SessionVO> sessionVOList = page.getRecords().stream().map(aiChatSession -> {
            SessionVO sessionVO = new SessionVO();
            sessionVO.setSessionId(aiChatSession.getId());
            sessionVO.setTitle(aiChatSession.getTitle());
            sessionVO.setCreateAt(aiChatSession.getCreatedAt());
            return sessionVO;
        }).collect(Collectors.toList());
        return Result.success(PageResult.of(page.getTotal(), page.getSize(), sessionVOList));
    }

    @Override
    public Result<Void> renameSession(Long sessionId, String title) {
        Long userId = UserContext.getUserId();
        // 判断是否是当前用户的会话
        AiChatSession aiChatSession = getOne(new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getUserId, userId)
                .eq(AiChatSession::getId, sessionId));
        if (aiChatSession == null) {
            return Result.fail("会话不存在");
        }
        // 重命名会话标题
        aiChatSession.setTitle(title);
        updateById(aiChatSession);
        return Result.success();
    }
}
