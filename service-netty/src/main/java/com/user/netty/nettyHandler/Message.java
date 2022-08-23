package com.user.netty.nettyHandler;


import com.user.model.domain.ChatRecord;
import lombok.Data;

/**
 * @author ice
 * @date 2022/7/25 16:14
 */
@Data
public class Message {
    private Integer type;// 消息的类型
    private ChatRecord chatRecord;//聊天的消息
    private Object ext; // 扩展
}
