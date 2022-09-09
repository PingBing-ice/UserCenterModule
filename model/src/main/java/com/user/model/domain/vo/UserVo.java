package com.user.model.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户消息脱敏
 * </p>
 *
 * @author ice
 * @since 2022-06-14
 */
@Data
@ApiModel(value = "User对象", description = "用户表")
public class UserVo implements Serializable{

    private static final long serialVersionUID = -6204388767292859512L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("登陆账号")
    private String userAccount;

    @ApiModelProperty("用户头像")
    private String avatarUrl;

    @ApiModelProperty("性别")
    private String gender;


    /**
     * 标签
     */
    private String tags;

    /**
     * 个人描述
     */
    private String profile;

    @ApiModelProperty("手机号")
    private String tel;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("用户状态")
    private Integer userStatus;

    @ApiModelProperty("用户角色 ,判断是否是管理员")
    private Integer role;

    @ApiModelProperty("成员编号")
    private String planetCode;


    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;



}
