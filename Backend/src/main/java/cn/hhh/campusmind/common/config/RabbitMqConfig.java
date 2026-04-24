package cn.hhh.campusmind.common.config;

import cn.hhh.campusmind.common.constants.RabbbitMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public DirectExchange studentProfileUpdateExchange() {
        return new DirectExchange(RabbbitMqConstants.STUDENT_PROFILE_UPDATE_EXCHANGE, true, false);
    }

    @Bean
    public Queue studentProfileUpdateQueue() {
        return new Queue(RabbbitMqConstants.STUDENT_PROFILE_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding studentProfileUpdateBinding(Queue studentProfileUpdateQueue, DirectExchange studentProfileUpdateExchange) {
        return BindingBuilder.bind(studentProfileUpdateQueue)
                .to(studentProfileUpdateExchange)
                .with(RabbbitMqConstants.STUDENT_PROFILE_UPDATE_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        // 消息持久化
        rabbitTemplate.setBeforePublishPostProcessors(m -> MessageBuilder
                .fromMessage(m)
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build());
        return rabbitTemplate;
    }
}
