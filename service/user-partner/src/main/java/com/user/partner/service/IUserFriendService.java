package com.user.partner.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.user.model.domain.User;
import com.user.model.domain.UserFriend;


import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ice
 * @since 2022-07-28
 */
public interface IUserFriendService extends IService<UserFriend> {
    /**
     * 接收好友请求
     * @param reqId
     */
    void addFriendReq(String reqId,String userId);

    List<User> selectFriend(String userId);
}
