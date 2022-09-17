package com.user.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 标签表
 * </p>
 *
 * @author ice
 * @since 2022-09-16
 */
@TableName("user_label")
@ApiModel(value = "UserLabel对象", description = "标签表")
@Data
public class UserLabel implements Serializable {


    private static final long serialVersionUID = 4755244273531005010L;
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("标签类型")
    private String labelType;

    @ApiModelProperty("标签")
    private String label;

    @ApiModelProperty("是否删除")
    @TableLogic
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    private LocalDateTime updateTime;


}
