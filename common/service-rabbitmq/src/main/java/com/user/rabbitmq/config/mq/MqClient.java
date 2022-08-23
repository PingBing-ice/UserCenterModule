package com.user.rabbitmq.config.mq;

/**
 * @author ice
 * @date 2022/8/20 15:49
 */

public class MqClient {
    // 普通
    public static final String NETTY_EXCHANGE = "exchange_netty";
    public static final String NETTY_KEY = "netty";
    public static final String NETTY_QUEUE = "netty_queue";

    // 死信
    public static final String DIE_EXCHANGE ="exchange_die";
    public static final String DIE_QUEUE = "die_queue";
    public static final String DIE_KEY ="die_key";
}
