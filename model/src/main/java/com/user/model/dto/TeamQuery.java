package com.user.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ice
 * @date 2022/8/22 16:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 队伍的名称
     */
    private String name;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Long maxNum;


    /**
     * 状态
     */
    private String status;


}
