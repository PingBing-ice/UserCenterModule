package com.user.model.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author ice
 * @since 2022-07-28
 */
@TableName("user_friend")
@ApiModel(value = "UserFriend对象", description = "")
@Data
public class UserFriend implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("朋友id")
    private String friendsId;

    @ApiModelProperty("朋友备注")
    private String comments;

    @ApiModelProperty("添加好友日期")
    private LocalDateTime createTime;

    @ApiModelProperty("是否删除")
    @TableLogic
    private Integer isDelete;


}
