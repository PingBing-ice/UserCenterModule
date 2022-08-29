package com.user.partner.listener;

import com.rabbitmq.client.Channel;

import com.user.model.domain.ChatRecord;
import com.user.partner.service.IChatRecordService;

import com.user.rabbitmq.config.mq.MqClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ice
 * @date 2022/8/20 18:53
 */
@Component
@Slf4j
public class DieListener {
    @Autowired
    private IChatRecordService recordService;

    @RabbitListener(queues = MqClient.DIE_QUEUE)
    public void saveDieReceiver(Message message, Channel channel, ChatRecord chatRecord) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-HH-dd hh:mm");
        log.error("调用死信队列接收消息" + dateFormat.format(new Date()));
        boolean save = recordService.save(chatRecord);
        if (!save) {
            log.error("调用死信队列接收消息 接收失败"+dateFormat.format(new Date()));
        }
    }
}
