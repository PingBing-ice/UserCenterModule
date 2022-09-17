package com.user.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.user.model.domain.TeamChatRecord;
import com.user.model.domain.User;
import com.user.partner.mapper.TeamChatRecordMapper;
import com.user.partner.service.ITeamChatRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.util.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 队伍聊天记录表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-09-12
 */
@Service
public class TeamChatRecordServiceImpl extends ServiceImpl<TeamChatRecordMapper, TeamChatRecord>
        implements ITeamChatRecordService {

    @Override
    public List<TeamChatRecord> getTeamChatRecordByTeamId(HttpServletRequest request, String teamId) {
        User user = UserUtils.getLoginUser(request);
        if (!StringUtils.hasText(teamId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<TeamChatRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        return this.list(wrapper);
    }
}
