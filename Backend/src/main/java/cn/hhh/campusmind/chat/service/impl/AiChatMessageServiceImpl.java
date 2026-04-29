package cn.hhh.campusmind.chat.service.impl;

import cn.hhh.campusmind.chat.domain.dto.SendMessageDTO;
import cn.hhh.campusmind.chat.domain.enums.RoleEnum;
import cn.hhh.campusmind.chat.domain.messages.SaveContextAfterChatMessage;
import cn.hhh.campusmind.chat.domain.po.AiChatMessage;
import cn.hhh.campusmind.chat.domain.po.AiChatSession;
import cn.hhh.campusmind.chat.domain.vo.ChatMessageVO;
import cn.hhh.campusmind.chat.domain.vo.Http4SendMessage;
import cn.hhh.campusmind.chat.mapper.AiChatMessageMapper;
import cn.hhh.campusmind.chat.mapper.AiChatSessionMapper;
import cn.hhh.campusmind.chat.service.IAiChatMessageService;
import cn.hhh.campusmind.common.constants.HttpConstants;
import cn.hhh.campusmind.common.constants.RabbitMqConstants;
import cn.hhh.campusmind.common.page.PageQuery;
import cn.hhh.campusmind.common.page.PageResult;
import cn.hhh.campusmind.common.pool.ExecutorPool;
import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.common.utils.UserContext;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


/**
 * <p>
 * AI消息表：记录会话内每一轮消息 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatMessageServiceImpl extends ServiceImpl<AiChatMessageMapper, AiChatMessage> implements IAiChatMessageService {

    private final AiChatSessionMapper aiChatSessionMapper;
    private final ExecutorPool executorPool;
    private final OkHttpClient client;
    private final RabbitTemplate rabbitTemplate;

    @Value("${internal-api-token}")
    public String INTERNAL_API_TOKEN;

    @Override
    public Result<PageResult<ChatMessageVO>> getMessages(Long sessionId, PageQuery pageQuery) {
        Long userId = UserContext.getUserId();
        // 判断会话是否存在以及是否属于当前用户
        AiChatSession session = aiChatSessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            return Result.fail("会话不存在或不属于当前用户");
        }
        // 分页查询消息
        Page<AiChatMessage> page = lambdaQuery().eq(AiChatMessage::getSessionId, sessionId)
                .eq(AiChatMessage::getUserId, userId)
                .eq(AiChatMessage::getIsDelete, 0)
                .orderByDesc(AiChatMessage::getCreatedAt).page(pageQuery.toPage());
        if (page == null || page.getRecords().isEmpty()) {
            return Result.success(PageResult.of(0,0,List.of()));
        }
        // 转换为VO
        List<ChatMessageVO> voList = page.getRecords().stream()
                .map(msg -> {
                    ChatMessageVO vo = new ChatMessageVO();
                    vo.setSessionId(msg.getSessionId());
                    vo.setMessageId(msg.getId());
                    vo.setRole(msg.getRole());
                    vo.setContent(msg.getContent());
                    vo.setCreateAt(msg.getCreatedAt().toString());
                    return vo;
                })
                .toList();
        return Result.success(PageResult.of(page.getTotal(),page.getSize(),voList));
    }


    @Override
    public SseEmitter chatStream(SendMessageDTO sendMessageDTO) {
        Long userId = UserContext.getUserId();
        // 判断会话是否存在以及是否属于当前用户
        AiChatSession session = aiChatSessionMapper.selectById(sendMessageDTO.getSessionId());
        if (session == null || !session.getUserId().equals(userId)) {
            throw new IllegalArgumentException("会话不存在或不属于当前用户");
        }
        // 保存当前消息
        AiChatMessage msg = new AiChatMessage();
        msg.setSessionId(sendMessageDTO.getSessionId());
        msg.setUserId(userId);
        msg.setRole(RoleEnum.USER);
        msg.setContent(sendMessageDTO.getContent());
        save(msg);

        SseEmitter emitter = new SseEmitter(60_000L);

        executorPool.submit(() -> {
            try {
                forwardPythonSseByOkHttp(emitter, sendMessageDTO, client, userId);
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /**
     * 与python服务交互,流式获得ai发言
     * @param emitter
     * @param sendMessageDTO
     * @param client
     * @throws Exception
     */
    private void forwardPythonSseByOkHttp(SseEmitter emitter, SendMessageDTO sendMessageDTO, OkHttpClient client, Long userId) throws Exception {
        // 构建请求体
        Http4SendMessage body = new Http4SendMessage();
        body.setSessionId(sendMessageDTO.getSessionId().toString());
        body.setContent(sendMessageDTO.getContent());
        body.setUserId(userId);
        body.setTraceId(sendMessageDTO.getTraceId());

        Request request = new Request.Builder()
                .url(HttpConstants.CHAT_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "text/event-stream")
                .addHeader(HttpConstants.INTERNAL_API_TOKEN_NAME, INTERNAL_API_TOKEN)
                .post(RequestBody.create(
                        new GsonBuilder().create().toJson(body),
                        okhttp3.MediaType.Companion.parse("application/json; charset=utf-8")
                ))
                .build();
        // 保存完整的ai发言
        StringBuilder aiMessageBuilder = new StringBuilder();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                // 连接出现问题或请求出现问题,返回错误信息
                emitter.send(SseEmitter.event().name("error").data("python status=" + response.code()));
                return;
            }

            BufferedSource source = response.body().source();
            String event = "message";
            StringBuilder dataBuilder = new StringBuilder();

            while (!source.exhausted()) {
                String line = source.readUtf8LineStrict();
                // 空行：一条 SSE 事件结束
                if (line.isEmpty()) {
                    String data = dataBuilder.toString();
                    if (!data.isEmpty()) {
                        emitter.send(SseEmitter.event().name(event).data(data));
                        if ("done".equals(event)) {
                            emitter.complete();
                            break;
                        }
                    }
                    // 重置，准备下一条事件
                    event = "message";
                    dataBuilder.setLength(0);
                    continue;
                }
                if (line.startsWith("event:")) {
                    event = line.substring("event:".length()).trim();
                } else if (line.startsWith("data:")) {
                    String chunk = line.substring("data:".length());
                    if (!dataBuilder.isEmpty()) {
                        // 多行 data 之间按规范拼接换行
                        dataBuilder.append('\n');
                    }
                    dataBuilder.append(chunk);
                    if ("message".equals(event)) {
                        aiMessageBuilder.append(chunk);
                    }
                }
            }
        }
        // 发送mq消息,异步保存数据
        rabbitTemplate.convertAndSend(
                RabbitMqConstants.AI_INTERACTION_SAVE_MESSAGE_EXCHANGE,
                RabbitMqConstants.AI_INTERACTION_SAVE_MESSAGE_ROUTING_KEY,
                SaveContextAfterChatMessage.builder()
                        .sessionId(sendMessageDTO.getSessionId())
                        .aiMessage(aiMessageBuilder.toString())
                        .userId(userId)
                        .userMessage(sendMessageDTO.getContent())
                        .traceId(sendMessageDTO.getTraceId())
                        .build()
        );
    }
}
