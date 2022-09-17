package com.user.usercenter.service.impl;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.user.model.constant.RedisKey;
import com.user.model.domain.User;
import com.user.model.request.UserRegisterRequest;
import com.user.usercenter.mapper.UserMapper;
import com.user.usercenter.service.IUserService;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.util.utils.MD5;
import com.user.util.utils.TimeUtils;
import com.user.util.utils.UserUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.user.model.constant.UserConstant.ADMIN_ROLE;
import static com.user.model.constant.UserConstant.USER_LOGIN_STATE;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-06-14
 */
@Service
@Log4j2
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Autowired
    private StringRedisTemplate redisTemplate;



    @Override
    public Long userRegister(UserRegisterRequest userRegister) {
        String userAccount = userRegister.getUserAccount();
        String password = userRegister.getPassword();
        String checkPassword = userRegister.getCheckPassword();
        String planetCode = userRegister.getPlanetCode();
        if (!StringUtils.hasText(planetCode)) {
            planetCode = RandomUtil.randomInt(10, 10000) + "";
        }
        boolean hasEmpty = StrUtil.hasEmpty(userAccount, password, checkPassword, planetCode);
        if (hasEmpty) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        // 1. 校验
        if (StrUtil.hasEmpty(userAccount, password, checkPassword, planetCode)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 3) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "用户名过短");
        }
        if (password.length() < 6 || checkPassword.length() < 6) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        if (planetCode.length() > 5) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "编号过长");
        }
        // 校验账户不能包含特殊字符
        String pattern = "^([\\u4e00-\\u9fa5]+|[a-zA-Z0-9]+)$";
        Matcher matcher = Pattern.compile(pattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "账号特殊符号");
        }
        // 判断密码和和用户名是否相同
        if (password.equals(userAccount)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "账号密码相同");
        }
        if (!password.equals(checkPassword)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "确认密码错误");
        }
        // 判断用户是否重复
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", userAccount);
        Long aLong = baseMapper.selectCount(wrapper);
        if (aLong > 0) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "注册用户重复");
        }
        // 判断用户是否重复
        wrapper = new QueryWrapper<>();
        wrapper.eq("planet_code", planetCode);
        Long a = baseMapper.selectCount(wrapper);
        if (a > 0) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "注册用户编号重复");
        }
        // 加密密码
        String passwordMD5 = MD5.getMD5(password);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPassword(passwordMD5);
        user.setPlanetCode(planetCode);
        user.setAvatarUrl("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif?imageView2/1/w/80/h/80");
        user.setUsername(userAccount);
        boolean save = this.save(user);
        if (!save) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "注册用户失败");
        }
        return Long.parseLong(user.getId());
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        // 1. 校验
        if (userAccount.length() < 3) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (password.length() < 6) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        // 校验账户不能包含特殊字符
        String pattern = "^([\\u4e00-\\u9fa5]+|[a-zA-Z0-9]+)$";
        Matcher matcher = Pattern.compile(pattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }
        String passwordMD5 = MD5.getMD5(password);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", userAccount);
        wrapper.eq("password", passwordMD5);
        User user = baseMapper.selectOne(wrapper);

        if (user == null) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "账号密码错误");
        }

        // 用户脱敏

        User cleanUser = getSafetyUser(user);
        // 记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, cleanUser);

        return cleanUser;
    }

    /**
     * 用户脱敏
     */
    @Override
    public User getSafetyUser(User user) {
        if (user == null) {
            return null;
        }
        User cleanUser = new User();
        cleanUser.setId(user.getId());
        cleanUser.setUsername(user.getUsername());
        cleanUser.setUserAccount(user.getUserAccount());
        cleanUser.setAvatarUrl(user.getAvatarUrl());
        cleanUser.setGender(user.getGender());
        cleanUser.setTel(user.getTel());
        cleanUser.setEmail(user.getEmail());
        cleanUser.setUserStatus(user.getUserStatus());
        cleanUser.setCreateTime(user.getCreateTime());
        cleanUser.setRole(user.getRole());
        cleanUser.setPlanetCode(user.getPlanetCode());
        cleanUser.setTags(user.getTags());
        cleanUser.setProfile(user.getProfile());
        return cleanUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);

        return user != null && Objects.equals(user.getRole(), ADMIN_ROLE);
    }

    /**
     * 用户注销
     *
     * @param request 1
     */
    @Override
    public void userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @Override
    public Integer updateUser(User user, HttpServletRequest request) {
        if (user == null) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        boolean admin = isAdmin(request);
        if (!admin) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "你不是管理员");
        }
        int update = baseMapper.updateById(user);
        if (update <= 0) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "修改失败");
        }
        return update;
    }


    /**
     * ===============================================================
     * 根据标签搜索用户
     *
     * @return 返回用户列表
     */
    @Override
    public List<User> searchUserTag(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        // sql 语句查询
//        QueryWrapper<User> wrapper = new QueryWrapper<>();
//        // 拼接and 查询
//        for (String tagName : tagNameList) {
//            wrapper = wrapper.like("tags", tagName);
//        }
//        List<User> userList = baseMapper.selectList(wrapper);
        // 内存查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        List<User> userList = baseMapper.selectList(wrapper);
        Gson gson = new Gson();

        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            // 将json 数据解析成 Set
            Set<String> tempTagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (tempTagNameSet.contains(tagName)) {
                    return true;
                }
            }
            return false;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }


    @Override
    public Map<String, Object> selectPageIndexList(long current, long size) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        Page<User> commentPage = baseMapper.selectPage(new Page<>(current, size), wrapper);
        Map<String, Object> map = new HashMap<>();
        List<User> userList = commentPage.getRecords();
        // 通过stream 流的方式将列表里的每个user进行脱敏
        userList = userList.parallelStream().peek(this::getSafetyUser).collect(Collectors.toList());
        map.put("items", userList);
        map.put("current", commentPage.getCurrent());
        map.put("pages", commentPage.getPages());
        map.put("size", commentPage.getSize());
        map.put("total", commentPage.getTotal());
        map.put("hasNext", commentPage.hasNext());
        map.put("hasPrevious", commentPage.hasPrevious());
        return map;
    }

    /**
     * 根据用户修改资料
     */
    @Override
    public long getUserByUpdateID(User loginUser, User updateUser) {
        String userId = updateUser.getId();
        String username = updateUser.getUsername();
        String gender = updateUser.getGender();
        String tel = updateUser.getTel();
        String email = updateUser.getEmail();
        Integer isDelete = updateUser.getIsDelete();
        String tags = updateUser.getTags();

        if (isDelete != null) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "SB");
        }

        if (!StringUtils.hasText(userId) || Long.parseLong(userId) <= 0) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }

        if (!StringUtils.hasText(username) && !StringUtils.hasText(tel) &&
                !StringUtils.hasText(email) && !StringUtils.hasText(tags)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.hasText(gender) && !gender.equals("男") && !gender.equals("女")) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        if (!isAdmin(loginUser) && !userId.equals(loginUser.getId())) {
            throw new GlobalException(ErrorCode.NO_AUTH);
        }
        User oldUser = baseMapper.selectById(userId);
        if (oldUser == null) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }

        if (StringUtils.hasText(tags)) {
            if (!oldUser.getTags().equals(tags)) {
                return this.TagsUtil(userId, updateUser);
            }else {
                throw new GlobalException(ErrorCode.NULL_ERROR,"重复提交...");
            }
        }
        return baseMapper.updateById(updateUser);

    }

    public boolean isAdmin(User user) {
        return user != null && Objects.equals(user.getRole(), ADMIN_ROLE);
    }

    @Override
    public List<User> friendUserName(String userID, String friendUserName) {
        if (!StringUtils.hasText(friendUserName)) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("user_account", friendUserName);
        List<User> userList = baseMapper.selectList(userQueryWrapper);
        if (userList.size() == 0) {
            throw new GlobalException(ErrorCode.NULL_ERROR, "查无此人");
        }
        userList = userList.stream().filter(user -> !userID.equals(user.getId())).collect(Collectors.toList());
        userList.forEach(this::getSafetyUser);
        return userList;

    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new GlobalException(ErrorCode.NO_LOGIN);
        }
        return user;
    }

    public int TagsUtil(String userId, User updateUser) {
        String tagKey = RedisKey.tagRedisKey + userId;
        String tagNum =  redisTemplate.opsForValue().get(tagKey);
        if (!StringUtils.hasText(tagNum)) {
            int i = baseMapper.updateById(updateUser);
            if (i <= 0) {
                throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "保存失败...");
            }
            redisTemplate.opsForValue().set(tagKey, "1",
                    TimeUtils.getRemainSecondsOneDay(new Date()), TimeUnit.SECONDS);
            return i;
        }
        int parseInt = Integer.parseInt(tagNum);
        if (parseInt > 5) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "今天修改次数以上限...");
        }
        int i = baseMapper.updateById(updateUser);
        if (i <= 0) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "保存失败...");
        }
        redisTemplate.opsForValue().increment(tagKey);
        Boolean hasKey = redisTemplate.hasKey(RedisKey.redisIndexKey);
        if (Boolean.TRUE.equals(hasKey)) {
            redisTemplate.delete(RedisKey.redisIndexKey);
        }
        return i;
    }

    @Override
    public Map<String, Object> searchUser(HttpServletRequest request, String username, Long current, Long size) {
        boolean admin = this.isAdmin(request);
        if (!admin) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "你不是管理员");
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 如果name有值
        if (StringUtils.hasText(username)) {
            wrapper.like("username", username);
        }
        if (current == null || size == null) {
            current = 1L;
            size = 30L;
        }
        Page<User> page = new Page<>(current, size);
        Page<User> userPage = baseMapper.selectPage(page, wrapper);
        userPage.getRecords().forEach(this::getSafetyUser);
        Map<String, Object> map = new HashMap<>();
        map.put("records", userPage.getRecords());
        map.put("current", userPage.getCurrent());
        map.put("total", userPage.getTotal());
        return map;
    }

    @Override
    public List<User> searchUserTag(String tag, HttpServletRequest request) {
        if (!StringUtils.hasText(tag)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        UserUtils.getLoginUser(request);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("tags", tag);
        Page<User> commentPage = baseMapper.selectPage(new Page<>(1, 200), wrapper);
        List<User> list = commentPage.getRecords();
        list.parallelStream().forEach(this::getSafetyUser);
        return list;
    }
}
