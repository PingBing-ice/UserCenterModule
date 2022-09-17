package com.user.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.model.domain.User;
import com.user.model.domain.UserTeam;
import com.user.openfeign.UserOpenFeign;
import com.user.partner.mapper.UserTeamMapper;
import com.user.partner.service.UserTeamService;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.util.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
* @author BING
* @description 针对表【user_team(队伍表)】的数据库操作Service实现
* @createDate 2022-08-22 15:55:33
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {
    @Autowired
    private UserOpenFeign userOpenFeign;

    @Override
    public List<User> getUserTeamById(String teamId, HttpServletRequest request) {
        if (!StringUtils.hasText(teamId)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        UserUtils.getLoginUser(request);
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        List<UserTeam> userTeams = this.list(wrapper);
        if (userTeams == null || userTeams.size() <= 0) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        List<String> userIdList = new ArrayList<>();
        userTeams.forEach(userTeam -> {
            String userId = userTeam.getUserId();
            userIdList.add(userId);
        });
        List<User> users = userOpenFeign.getListByIds(userIdList);
        if (users == null || users.size() <= 0) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR);
        }
        users.forEach(UserUtils::getSafetyUser);
        return users;
    }
}




