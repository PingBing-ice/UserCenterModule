package com.user.partner.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.model.domain.User;
import com.user.model.domain.UserFriendReq;
import com.user.partner.mapper.UserFriendReqMapper;
import com.user.partner.service.IUserFriendReqService;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.util.openfeign.UserOpenFeign;
import com.user.util.utils.SafetyUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-07-28
 */
@Service
public class UserFriendReqServiceImpl extends ServiceImpl<UserFriendReqMapper, UserFriendReq> implements IUserFriendReqService {
    @Autowired
    private UserOpenFeign userOpenFeign;

    @Override
    public int sendRequest(String fromUserId, String toUserId) {
        QueryWrapper<UserFriendReq> wrapper = new QueryWrapper<>();
        wrapper.eq("from_userid", fromUserId);
        wrapper.eq("to_userid", toUserId);
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        UserFriendReq userFriendReq = new UserFriendReq();

        userFriendReq.setFromUserid(fromUserId);
        userFriendReq.setToUserid(toUserId);
        return baseMapper.insert(userFriendReq);
    }

    @Override
    public List<User> checkFriend(String userId) {
        QueryWrapper<UserFriendReq> wrapper = new QueryWrapper<>();
        wrapper.eq("to_userid", userId);
        List<UserFriendReq> friendReqList = baseMapper.selectList(wrapper);
        friendReqList= friendReqList.stream().filter(userFriendReq -> userFriendReq.getUserStatus() == 0).collect(Collectors.toList());
        if (friendReqList.isEmpty()) {
            return null;
        }

        ArrayList<String> list = new ArrayList<>();
        for (UserFriendReq userFriendReq : friendReqList) {
            String fromUserid = userFriendReq.getFromUserid();
            list.add(fromUserid);
        }
        List<User> users = userOpenFeign.getListByIds(list);
        if (users.isEmpty()) {
            throw new RuntimeException("查找申请的用户为空");
        }
        return users.stream().peek(SafetyUserUtils::getSafetyUser).collect(Collectors.toList());

    }

    @Override
    public int Reject(String id) {
        UserFriendReq userFriendReq = new UserFriendReq();
        QueryWrapper<UserFriendReq> wrapper = new QueryWrapper<>();
        userFriendReq.setUserStatus(1);
        wrapper.eq("from_userid", id);
        return baseMapper.update(userFriendReq, wrapper);
    }


}
