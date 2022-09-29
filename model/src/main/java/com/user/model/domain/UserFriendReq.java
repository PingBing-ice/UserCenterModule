package com.user.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author ice
 * @since 2022-07-28
 */
@TableName("user_friend_req")
@ApiModel(value = "UserFriendReq对象", description = "")
@Data
public class UserFriendReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @ApiModelProperty("请求用户id")
    private String fromUserid;

    @ApiModelProperty("被请求好友用户")
    private String toUserid;

    @ApiModelProperty("发送的消息")
    private String message;

    @ApiModelProperty("消息是否已处理 0 未处理")
    private Integer userStatus;

    private LocalDateTime createTime;


}
