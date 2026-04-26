package cn.hhh.campusmind.common.mq;

import cn.hhh.campusmind.chat.domain.enums.RoleEnum;
import cn.hhh.campusmind.chat.domain.messages.GenerateSeesionTitleMessage;
import cn.hhh.campusmind.chat.domain.messages.SaveContextAfterChatMessage;
import cn.hhh.campusmind.chat.domain.po.AiChatMessage;
import cn.hhh.campusmind.chat.domain.po.AiChatSession;
import cn.hhh.campusmind.chat.service.IAiChatMessageService;
import cn.hhh.campusmind.chat.service.IAiChatSessionService;
import cn.hhh.campusmind.common.constants.RabbitMqConstants;
import cn.hhh.campusmind.common.constants.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static cn.hhh.campusmind.common.constants.RabbitMqConstants.GENERATE_SESSION_TITLE_EXCHANGE;
import static cn.hhh.campusmind.common.constants.RabbitMqConstants.GENERATE_SESSION_TITLE_ROUTING_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveContextAfterChatConsumer {

    private final IAiChatMessageService aiChatMessageService;
    private final StringRedisTemplate redisTemplate;
    private final IAiChatSessionService aiChatSessionService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMqConstants.AI_INTERACTION_SAVE_MESSAGE_QUEUE, durable = "true"),
            exchange = @Exchange(name = RabbitMqConstants.AI_INTERACTION_SAVE_MESSAGE_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = RabbitMqConstants.AI_INTERACTION_SAVE_MESSAGE_ROUTING_KEY
    ))
    public void saveContextAfterChat(SaveContextAfterChatMessage message) {
        log.info("消费ai交互保存消息: {}", message);
        // 保存ai消息
        AiChatMessage aiMsg = new AiChatMessage();
        aiMsg.setSessionId(message.getSessionId());
        aiMsg.setUserId(message.getUserId());
        aiMsg.setRole(RoleEnum.AI);
        aiMsg.setContent(message.getAiMessage());
        aiMsg.setTraceId(message.getTraceId());
        aiMsg.setContentType(message.getContentType());
        aiChatMessageService.save(aiMsg);

        AiChatSession session = aiChatSessionService.getById(message.getSessionId());

        Long userId = message.getUserId();
        LocalDateTime now = LocalDateTime.now();
        // 把上下文存入redis
        redisTemplate.opsForZSet().add(RedisConstants.USER_SESSION_CONTEXT + userId + ":" + session.getId(),
                "human:" + message.getUserMessage(),
                now.toLocalTime().getSecond());
        redisTemplate.opsForZSet().add(RedisConstants.USER_SESSION_CONTEXT + userId + ":" + session.getId(),
                "assistant:" + message.getAiMessage(),now.toLocalTime().getSecond() + 1);
        redisTemplate.expire(RedisConstants.USER_SESSION_CONTEXT + userId + ":" + session.getId(),
                RedisConstants.USER_SESSION_CONTEXT_EXPIRE, TimeUnit.HOURS);
        // TODO更新会话状态 (或许还有字段需要更新?)
        aiChatSessionService.lambdaUpdate()
                        .set(AiChatSession::getLastMessageAt, aiMsg.getCreatedAt())
                        .set(AiChatSession::getMessageCount, aiChatSessionService.getById(message.getSessionId()).getMessageCount() + 2)
                        .eq(AiChatSession::getId, message.getSessionId())
                        .update();

        // 判断该会话是否有标题
        if (session.getTitle() == null || session.getTitle().isEmpty()) {
            // 发送消息给ai,根据当前会话信息,总结生成会话标题
            log.info("发送生成会话标题消息: {}", session.getId());
            rabbitTemplate.convertAndSend(
                    GENERATE_SESSION_TITLE_EXCHANGE,
                    GENERATE_SESSION_TITLE_ROUTING_KEY,
                    GenerateSeesionTitleMessage.builder().sessionId(session.getId()).userId(userId).build()
            );
        }
    }
}
