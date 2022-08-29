package com.user.rabbitmq.config.mq;


import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ice
 * @date 2022/8/20 11:23
 */
@Configuration
public class RabbitMqConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean("nettyExchange")
    public DirectExchange NettyExchange() {
        return new DirectExchange(MqClient.NETTY_EXCHANGE);
    }

    @Bean("nettyQueue")
    public Queue NettyQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MqClient.DIE_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MqClient.DIE_KEY);
        // 设置过期时间 10 秒
        arguments.put("x-message-ttl", 10000);
        return QueueBuilder.durable(MqClient.NETTY_QUEUE).withArguments(arguments).build();
    }

    @Bean
    public Binding bindingNetty(@Qualifier("nettyExchange") DirectExchange nettyExchange,
                                @Qualifier("nettyQueue") Queue nettyQueue) {
        return BindingBuilder.bind(nettyQueue).to(nettyExchange).with(MqClient.NETTY_KEY);
    }

    @Bean("dieExchange")
    public DirectExchange dieExchange() {
        return new DirectExchange(MqClient.DIE_EXCHANGE);
    }

    @Bean("dieQueue")
    public Queue dieQueue() {
        return QueueBuilder.durable(MqClient.DIE_QUEUE).build();
    }

    @Bean
    public Binding dieBinding(@Qualifier("dieExchange") DirectExchange dieExchange,
                              @Qualifier("dieQueue") Queue dieQueue) {
        return BindingBuilder.bind(dieQueue).to(dieExchange).with(MqClient.DIE_KEY);
    }
    @Bean("redisQueue")
    public Queue redisQueue() {
        return QueueBuilder.durable(MqClient.REDIS_QUEUE).build();
    }
    @Bean
    public Binding redisBinding(@Qualifier("nettyExchange") DirectExchange dieExchange,
                                @Qualifier("redisQueue") Queue redisQueue) {
        return BindingBuilder.bind(redisQueue).to(dieExchange).with(MqClient.REDIS_KEY);
    }
}
