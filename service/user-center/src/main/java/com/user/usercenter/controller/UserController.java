package com.user.usercenter.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.user.model.domain.User;
import com.user.model.request.UserLoginRequest;
import com.user.model.request.UserRegisterRequest;
import com.user.rabbitmq.config.mq.RabbitService;
import com.user.usercenter.service.IUserService;
import com.user.util.common.B;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.user.model.constant.UserConstant.USER_LOGIN_STATE;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ice
 * @since 2022-06-14
 */
@RestController
@RequestMapping("/user")
//@CrossOrigin(origins = {"http://localhost:7777","http://127.0.0.1:7777"}, allowCredentials = "true")
@SuppressWarnings("all")
@Slf4j
public class UserController {

    @Resource
    private IUserService userService;

    @Autowired
    private RabbitService rabbitService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/test")
    public B<String> test() {
        return B.ok("请求成功");
    }


    // 用户注册
    @PostMapping("/Register")
    public B<Long> userRegister(@RequestBody UserRegisterRequest userRegister) {
        log.info("用户注册!!!!!!!!!!");
        if (userRegister == null) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRegister.getUserAccount();
        String password = userRegister.getPassword();
        String checkPassword = userRegister.getCheckPassword();
        String planetCode = userRegister.getPlanetCode();
        boolean hasEmpty = StrUtil.hasEmpty(userAccount, password, checkPassword, planetCode);
        if (hasEmpty) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        Long aLong = userService.userRegister(userAccount, password, checkPassword, planetCode);

        return B.ok(aLong);
    }

    /**
     * 获取当前的登录信息
     *
     * @return 返回用户
     */
    @GetMapping("/current")
    public B<User> getCurrent(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null) {
            throw new GlobalException(ErrorCode.NO_LOGIN, "请先登录!!!!");
        }
        String id = currentUser.getId();
        // todo 校验用户是否合法
        User user = userService.getById(id);
        // 进行脱敏
        User safetyUser = userService.getSafetyUser(user);
        return B.ok(safetyUser);
    }

    // 用户登录
    @PostMapping("/Login")
    public B<User> userLogin(@RequestBody UserLoginRequest userLogin, HttpServletRequest request) {
        if (userLogin == null) {
            throw new GlobalException(ErrorCode.NULL_ERROR, "数据为空!");
        }
        String userAccount = userLogin.getUserAccount();
        String password = userLogin.getPassword();
        boolean hasEmpty = StrUtil.hasEmpty(userAccount, password);
        if (hasEmpty) {
            throw new GlobalException(ErrorCode.NULL_ERROR, "账号密码为空!");
        }
        User user = userService.userLogin(userAccount, password, request);
        return B.ok(user);
    }

    // 查询用户
    @GetMapping("/searchUser")
    public B<List<User>> searchUser(String username, HttpServletRequest request) {
        log.info("查询用户!!!!!!!!!!");
        boolean admin = userService.isAdmin(request);
        if (!admin) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "你不是管理员");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 如果name有值
        if (StringUtils.isNotBlank(username)) {
            wrapper.like("username", username);
        }
        List<User> list = userService.list(wrapper);
        // 通过stream 流的方式将列表里的每个user进行脱敏
        list = list.stream().peek(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return B.ok(list);
    }
    // 管理员删除用户
    @PostMapping("/delete")
    public B<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        if (id <= 0) {
            return B.error(ErrorCode.NULL_ERROR);
        }
        boolean admin = userService.isAdmin(request);
        if (!admin) {
            return B.error(ErrorCode.NO_AUTH);
        }
        boolean removeById = userService.removeById(id);
        return B.ok(removeById);
    }

    /**
     * 修改用户
     */
    @PostMapping("/UpdateUser")
    public B<Integer> UpdateUser(User user, HttpServletRequest request) {
        Integer is = userService.updateUser(user, request);
        return B.ok(is);
    }

    /**
     * 用户注销
     */
    //
    @PostMapping("/Logout")
    public B<Integer> userLogout(HttpServletRequest request) {
        log.info("用户注销");
        if (request == null) {
            return B.error(ErrorCode.NULL_ERROR);
        }
        userService.userLogout(request);
        return B.ok();
    }


    /**
     * ===================================================================================================
     * 根据标签的搜索用户
     */
    @GetMapping("/search/tags")
    public B<List<User>> getSearchUserTag(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserTag(tagNameList);
        return B.ok(userList);
    }

    /**
     * 修改用户
     */
    @PostMapping("/update")
    public B<Long> updateUserByID(@RequestBody User updateUser, HttpServletRequest request) {
        if (request == null) {
            throw new GlobalException(ErrorCode.NO_LOGIN);
        }
        if (updateUser == null) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            throw new GlobalException(ErrorCode.NO_LOGIN);
        }
        long id = userService.getUserByUpdateID(loginUser, updateUser);
        return B.ok(id);
    }

    /**
     * 主页展示数据
     */
    @GetMapping("/recommend")
    public B<Map<String, Object>> recommendUser(@RequestParam(required = false) long current, long size, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("user:recommend:%s", loginUser.getId());
        Map<String,Object> userMap = (Map<String,Object>)redisTemplate.opsForValue().get(redisKey);
        if (userMap != null) {
            return B.ok(userMap);
        }
        Map<String,Object>map = userService.selectPageIndexList(current, size);
        try {
            redisTemplate.opsForValue().set(redisKey, map, 180, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("缓存失败!!");
            log.error(e.getMessage());
        }
        return B.ok(map);
    }

    // ========================================================================================
    // 搜索用户
    @GetMapping("/searchUserName")
    public B<List<User>> searchUserName(@RequestParam(required = false) String friendUserName,
                                        HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new GlobalException(ErrorCode.NO_LOGIN);
        }
        String userId = user.getId();
        List<User> friendList = userService.friendUserName(userId, friendUserName);
        if (friendList.size() == 0) {
            return B.error(ErrorCode.NULL_ERROR);
        }
        return B.ok(friendList);
    }
}
