package com.user.model.constant;

/**
 * @author ice
 * @date 2022/9/10 16:59
 */

public interface RedisKey {
    String selectFriend = "selectFriendList::";
    String tagRedisKey = "tagNum::";

    String ossAvatarUserRedisKey = "ossAvatar:User:";
    String ossAvatarTeamRedisKey = "ossAvatar:Team:";

    String redisIndexKey = "user:recommend";
    String redisAddTeamLock = "user:addTeam:key";
    String redisFileAvatarLock = "user:file:avatar:key";
    String redisFileByTeamAvatarLock = "user:file:avatar:team:key";

}
