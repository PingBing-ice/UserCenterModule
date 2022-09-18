package com.user.usercenter.service.impl;

import com.user.model.domain.UserNotice;
import com.user.usercenter.mapper.UserNoticeMapper;
import com.user.usercenter.service.IUserNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 公告表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-09-18
 */
@Service
public class UserNoticeServiceImpl extends ServiceImpl<UserNoticeMapper, UserNotice> implements IUserNoticeService {

}
