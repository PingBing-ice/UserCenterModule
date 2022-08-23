package com.user.partner.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.model.domain.User;
import com.user.model.domain.UserFriend;
import com.user.partner.mapper.UserFriendMapper;
import com.user.partner.service.IUserFriendService;
import com.user.util.openfeign.UserOpenFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-07-28
 */
@Service
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend> implements IUserFriendService {
    @Resource
    private UserOpenFeign userOpenFeign;

    @Override
    @Transactional
    public void addFriendReq(String reqId,String userId) {

        UserFriend userFriend = new UserFriend();
        userFriend.setUserId(userId);
        userFriend.setFriendsId(reqId);
        int insert = baseMapper.insert(userFriend);
        if (insert <= 0) {
            throw new RuntimeException("添加好友失败");
        }
    }

    @Override
    public List<User> selectFriend(String userId) {
        QueryWrapper<UserFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).or().eq("friends_id", userId);
        List<UserFriend> userFriends = baseMapper.selectList(wrapper);
        List<String> userIdByList = new ArrayList<>();
        userFriends.forEach(userFriend -> {
            if (userFriend.getUserId().equals(userId)) {
                userIdByList.add(userFriend.getFriendsId());
            }
            if (userFriend.getFriendsId().equals(userId)) {
                userIdByList.add(userFriend.getUserId());
            }
        });
        return userOpenFeign.getListByIds(userIdByList);
    }
}
