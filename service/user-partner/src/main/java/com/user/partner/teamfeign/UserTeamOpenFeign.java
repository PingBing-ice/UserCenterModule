package com.user.partner.teamfeign;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.user.model.domain.Team;
import com.user.model.domain.UserTeam;
import com.user.partner.service.TeamService;
import com.user.partner.service.UserTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ice
 * @date 2022/9/12 19:19
 */
@RestController
@RequestMapping("/userTeam")
public class UserTeamOpenFeign {

    @Autowired
    private UserTeamService userTeamService;

    @Autowired
    private TeamService teamService;

    @GetMapping("/getUserTeamListById")
    public List<String> getUserTeamListById(@RequestParam("teamId") String teamId,@RequestParam("userId")String userId) {
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        List<UserTeam> list = userTeamService.list(wrapper);
        List<String> teamIdList = new ArrayList<>();
        list.forEach(userTeam -> {
            String teamUserId = userTeam.getUserId();
            if (!userId.equals(teamUserId)) {
                teamIdList.add(teamUserId);
            }
        });
        return teamIdList;
    }
    @GetMapping("/getTeamByUserId")
    public Team getTeamByTeamUser(@RequestParam("teamId") String teamId,@RequestParam("userId")String userId) {
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("team_id", teamId);
        return teamService.getOne(wrapper);
    }

    @PostMapping("/updateTeamByTeam")
    public boolean updateTeamByTeam(@RequestBody Team team) {
        if (team == null) {
            return false;
        }
        return teamService.updateById(team);
    }
}
