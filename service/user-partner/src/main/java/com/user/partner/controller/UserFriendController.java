package com.user.partner.controller;


import com.user.model.constant.RedisKey;
import com.user.model.constant.UserConstant;
import com.user.model.domain.User;
import com.user.model.resp.FriendUserResponse;
import com.user.partner.service.IUserFriendReqService;
import com.user.partner.service.IUserFriendService;
import com.user.util.common.B;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.util.utils.ThreadUtil;
import com.user.util.utils.UserUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ice
 * @since 2022-07-28
 */
@RestController
@RequestMapping("/friend/userFriend")
//@CrossOrigin(origins = {"http://localhost:7777"}, allowCredentials = "true")
@Log4j2
public class UserFriendController {
    @Autowired
    private IUserFriendService friendService;
    @Autowired
    private IUserFriendReqService friendReqService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    // 添加好友
    @GetMapping("/friendUser")
    public B<String> friendRequest(@RequestParam(required = false)String toUserId, HttpServletRequest request) {
        User loginUser = UserUtils.getLoginUser(request);
        String userId = loginUser.getId();
        int id = friendReqService.sendRequest(userId, toUserId);
        if (id != 1) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "发送失败");
        }
        String redisKey = RedisKey.selectFriend + userId;
        Boolean isKey = redisTemplate.hasKey(redisKey);
        if (Boolean.TRUE.equals(isKey)) {
            redisTemplate.delete(redisKey);
        }
        return B.ok();
    }


    // 查看好友申请
    @GetMapping("/checkFriend")
    public B<List<User>> CheckFriendRequests(HttpServletRequest request) {
        User user = UserUtils.getLoginUser(request);
        String userId = user.getId();
        List<User> users = friendReqService.checkFriend(userId);
        if (users == null) {
            return B.error(ErrorCode.NULL_ERROR);
        }
        return B.ok(users);
    }

    @GetMapping("/rejectFriend")
    public B<Integer> rejectFriend(@RequestParam(required = false) String id) {
        log.info("拒绝好友");
        if (!StringUtils.hasLength(id)) {
            return B.error(ErrorCode.NULL_ERROR);
        }
        int i = friendReqService.Reject(id);
        if (i <= 0) {
            return B.error(ErrorCode.SYSTEM_EXCEPTION);
        }
        return B.ok(i);
    }


    /**
     * 接收好友请求
     */
    @GetMapping("/acceptFriendReq")
    public B<String> acceptFriendReq(@RequestParam(required = false) String reqId, HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new GlobalException(ErrorCode.NO_LOGIN, "请先登录");
        }

        int reject = friendReqService.Reject(reqId);
        if (reject <= 0) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "添加好友失败");
        }
        String userId = user.getId();
        friendService.addFriendReq(reqId, userId);

        String redisKey = RedisKey.selectFriend + userId;
        Boolean isKey = redisTemplate.hasKey(redisKey);
        if (Boolean.TRUE.equals(isKey)) {
            redisTemplate.delete(redisKey);
        }
        return B.ok();
    }


    /**
     * 查找好友
     */
    @GetMapping("/selectFriendList")
    public B<List<User>> selectFriendList(HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new GlobalException(ErrorCode.NO_LOGIN, "请先登录");
        }
        String userId = user.getId();
        String redisKey = RedisKey.selectFriend + userId;
        List<User> userRedisList = (List<User>) redisTemplate.opsForValue().get(redisKey);
        if (!CollectionUtils.isEmpty(userRedisList)) {
            return B.ok(userRedisList);
        }

        List<User> userList = friendService.selectFriend(userId);
        if (!CollectionUtils.isEmpty(userList)) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    redisTemplate.opsForValue().set(redisKey, userList, 180, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("缓存失败");
                    log.error(e.getMessage());
                }
            }, ThreadUtil.getThreadPool());
            future.join();
        }
        return B.ok(userList);
    }

    /**
     * 查看好友详情
     * @param friendId
     * @return
     */
    @GetMapping("/getFriendUser")
    public B<FriendUserResponse> getFriendUser(@RequestParam("friendId")String friendId,HttpServletRequest request) {
        FriendUserResponse friendUser = friendService.getFriendUser(friendId, request);
        return B.ok(friendUser);
    }

    /**
     * 删除好友
     */
    @GetMapping("/delFriendUser")
    public B<Boolean> delFriendUser(@RequestParam("friendId") String friendId, HttpServletRequest request) {
        User loginUser = UserUtils.getLoginUser(request);
        String userId = loginUser.getId();
        boolean is = friendService.delFriendUser(friendId, userId);
        if (!is) {
            return B.error();
        }
        String redisKey = RedisKey.selectFriend + userId;
        Boolean isKey = redisTemplate.hasKey(redisKey);
        if (Boolean.TRUE.equals(isKey)) {
            redisTemplate.delete(redisKey);
        }
        return B.ok();
    }
}
