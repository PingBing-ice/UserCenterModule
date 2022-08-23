package com.user.partner.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.user.model.domain.User;
import com.user.model.domain.UserFriendReq;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ice
 * @since 2022-07-28
 */
public interface IUserFriendReqService extends IService<UserFriendReq> {
    int sendRequest(String fromUserId, String toUserId);

    List<User> checkFriend(String userId);

    int Reject(String id);


}
