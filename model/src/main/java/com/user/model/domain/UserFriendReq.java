package com.user.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getFromUserid() {
        return fromUserid;
    }

    public void setFromUserid(String fromUserid) {
        this.fromUserid = fromUserid;
    }
    public String getToUserid() {
        return toUserid;
    }

    public void setToUserid(String toUserid) {
        this.toUserid = toUserid;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserFriendReq{" +
            "id=" + id +
            ", fromUserid=" + fromUserid +
            ", toUserid=" + toUserid +
            ", message=" + message +
            ", userStatus=" + userStatus +
            ", createTime=" + createTime +
        "}";
    }
}
