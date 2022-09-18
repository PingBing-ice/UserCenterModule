package com.user.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.model.domain.TeamChatRecord;
import com.user.model.domain.User;
import com.user.model.domain.UserTeam;
import com.user.partner.mapper.TeamChatRecordMapper;
import com.user.partner.service.ITeamChatRecordService;
import com.user.partner.service.UserTeamService;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.util.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserTeamService userTeamService;

    @Override
    public List<TeamChatRecord> getTeamChatRecordByTeamId(HttpServletRequest request, String teamId) {
        if (!StringUtils.hasText(teamId)) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        User user = UserUtils.getLoginUser(request);
        String userId = user.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", userId);
        userTeamQueryWrapper.eq("team_id", teamId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count != 1) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR,"你以被踢出...");
        }

        QueryWrapper<TeamChatRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        return this.list(wrapper);
    }

    @Override
    public boolean deleteTeamChatRecordByTeamId(String teamId) {
        if (StringUtils.hasText(teamId)) {
            QueryWrapper<TeamChatRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("team_id", teamId);
            long count = this.count(wrapper);
            if (count == 0) return true;
            return this.remove(wrapper);
        }
        return false;
    }
}
