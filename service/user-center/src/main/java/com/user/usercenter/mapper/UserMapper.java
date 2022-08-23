package com.user.usercenter.mapper;

import com.user.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ice
 * @since 2022-06-14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
