package com.user.usercenter.listener;

import com.rabbitmq.client.Channel;
import com.user.model.domain.User;
import com.user.rabbitmq.config.mq.MqClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @Author ice
 * @Date 2022/9/28 9:42
 * @PackageName:com.user.usercenter.listener
 * @ClassName: RedisListener
 * @Description: 监听Redis的操作
 * @Version 1.0
 */
@Component
@Slf4j
public class RedisListener {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;



    @RabbitListener(queues = MqClient.REMOVE_REDIS_QUEUE)
    public void removeRedisByKey(Message message, Channel channel, String redisKey) {
        if (StringUtils.hasText(redisKey)) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                Boolean delete = redisTemplate.delete(redisKey);
                if (Boolean.TRUE.equals(delete)) {
                    log.info("删除redis =>  key: {} 成功",redisKey);
                }else {
                    log.error("删除redis =>  key: {} 失败",redisKey);
                }
            }
        } else {
            log.error("删除redis的key为空");
        }
    }
}
