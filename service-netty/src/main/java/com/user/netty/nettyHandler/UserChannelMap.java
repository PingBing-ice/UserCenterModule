package com.user.netty.nettyHandler;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ice
 * @date 2022/7/25 16:26
 */

public class UserChannelMap {
    // 用来保存用户id与通道的Map
    private static final Map<String, Channel> userChannelMap = new HashMap<>();


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
        if (!StringUtils.isEmpty(channelId)) {
            return;
        }

        for (String s : userChannelMap.keySet()) {
            Channel channel = userChannelMap.get(s);
            if (channel.id().asLongText().equals(channelId)) {
                System.out.println("用户Id移除连接: " + s);
                userChannelMap.remove(s);
                break;
            }
        }
    }

    public static void print() {
        for (String s : userChannelMap.keySet()) {
            System.out.println("用户id: " + s + " 通道: " + userChannelMap.get(s));
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
