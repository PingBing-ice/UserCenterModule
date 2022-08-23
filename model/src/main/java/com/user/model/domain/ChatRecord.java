package com.user.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 聊天记录表
 * </p>
 *
 * @author ice
 * @since 2022-07-28
 */
@TableName("chat_record")
@ApiModel(value = "ChatRecord对象", description = "聊天记录表")
@Data
public class ChatRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @ApiModelProperty("用户id ")
    private String userId;

    @ApiModelProperty("好友id")
    private String friendId;

    @ApiModelProperty("是否已读 0 未读")
    private Integer hasRead;

    private LocalDateTime createTime;

    @ApiModelProperty("是否删除")
    private Integer isDelete;

    @ApiModelProperty("消息")
    private String message;

}
