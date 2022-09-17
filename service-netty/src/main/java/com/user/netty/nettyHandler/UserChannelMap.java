package com.user.netty.nettyHandler;


import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ice
 * @date 2022/7/25 16:26
 */
@Slf4j
public class UserChannelMap {
    // 用来保存用户id与通道的Map
    private static final Map<String, Channel> userChannelMap = new ConcurrentHashMap<>();


    public static void put(String userId, Channel channel) {
        userChannelMap.put(userId, channel);
    }

    /**
     *  根据用户id 进行删除
     * @param userId 用户id
     */
    public static void remove(String userId) {
        userChannelMap.remove(userId);
    }

    /**
     *  根据通道Id 进行删除
     * @param channelId 通道ID
     */
    public static void removeByChannelId(String channelId) {
        if (!StringUtils.hasText(channelId)) {
            return;
        }
        for (String s : userChannelMap.keySet()) {
            Channel channel = userChannelMap.get(s);
            if (channel.id().asLongText().equals(channelId)) {
                log.error("用户Id移除连接: " + s);
                userChannelMap.remove(s);
                break;
            }
        }
    }

    public static void print() {
        for (String s : userChannelMap.keySet()) {
            System.out.println("用户id: " + s + " 通道: " + userChannelMap.get(s).id().asLongText());
        }
    }

    /**
     *  根据好友id 获取对应的通道
     * @param friendId
     * @return
     */
    public static Channel getFriendChannel(String friendId) {
        return userChannelMap.get(friendId);
    }
}
