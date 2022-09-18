package com.user.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 公告表
 * </p>
 *
 * @author ice
 * @since 2022-09-18
 */
@TableName("user_notice")
@ApiModel(value = "UserNotice对象", description = "公告表")
@Data
public class UserNotice implements Serializable {


    private static final long serialVersionUID = -242770780486132746L;
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("发布的公告")
    private String notice;

    @ApiModelProperty("发布的位置")
    private Integer region;

    @ApiModelProperty("是否删除")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;


}
