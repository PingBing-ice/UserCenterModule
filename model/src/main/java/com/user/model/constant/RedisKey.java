package com.user.model.constant;

/**
 * @author ice
 * @date 2022/9/10 16:59
 */

public interface RedisKey {
    String selectFriend = "selectFriendList::";
    String tagRedisKey = "tagNum::";
    String ossAvatarRedisKey = "ossAvatar::";
    String redisIndexKey = "user:recommend";
    String redisAddTeamLock = "user:addTeam:key";
    String redisFileAvatarLock = "user:file:avatar:key";
}
