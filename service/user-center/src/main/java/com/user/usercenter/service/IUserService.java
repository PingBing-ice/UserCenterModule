package com.user.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.model.domain.User;
import com.user.model.request.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author ice
 * @since 2022-06-14
 */
public interface IUserService extends IService<User> {


    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注册
     *
     * @param userAccount   账户
     * @param password      密码
     * @param checkPassword 校验码
     * @param planetCode 编号
     * @return 返回id值
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount 账户
     * @param password    密码
     * @return 用户信息
     */
    User userLogin(String userAccount, String password , HttpServletRequest request);

    /**
     *  用户脱敏
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     *  判断是否是管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     *  用户注销
     * @param request
     */
    void userLogout(HttpServletRequest request);

    /**
     * 修改用户
     * @param user
     * @return
     */
    Integer updateUser(User user,HttpServletRequest request);

    /**
     * ===============================================================
     * 根据标签搜索用户
     * @return
     */
    List<User> searchUserTag(List<String> tagList);

    /**
     *  修改用户
     * @param loginUser 登录的用户
     * @param updateUser 要修改的值
     * @return 4
     */
    long getUserByUpdateID(User loginUser, User updateUser);

    List<User> friendUserName(String userID, String friendUserName);

    Map<String, Object> selectPageIndexList(long current, long size);

    Map<String, Object> searchUser(HttpServletRequest request, String username, Long current, Long size);

    /**
     * 根据单个标签搜索用户
     * @param tag
     * @param request
     * @return
     */
    List<User> searchUserTag(String tag, HttpServletRequest request);

    List<User> matchUsers(long num, HttpServletRequest request);
}
