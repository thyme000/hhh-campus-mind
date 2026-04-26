package cn.hhh.campusmind.internal.service.impl;

import cn.hhh.campusmind.chat.domain.po.AiChatSession;
import cn.hhh.campusmind.chat.service.IAiChatSessionService;
import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.internal.domain.dto.AiSessionTitleResultDTO;
import cn.hhh.campusmind.internal.service.IInternalSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalSessionServiceImpl implements IInternalSessionService {

    private final IAiChatSessionService aiChatSessionService;

    /**
     * 回调ai生成的会话标题(更新会话标题)
     * @param titleDTO
     * @return
     */
    @Override
    public Result<Void> aiTitle(AiSessionTitleResultDTO titleDTO) {
        aiChatSessionService.lambdaUpdate()
                .eq(AiChatSession::getId, titleDTO.getSessionId())
                .set(AiChatSession::getTitle, titleDTO.getTitle())
                .update();
        return Result.success();
    }
}
