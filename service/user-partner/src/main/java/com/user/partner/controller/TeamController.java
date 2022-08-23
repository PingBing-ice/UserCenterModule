package com.user.partner.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.user.model.domain.Team;
import com.user.model.dto.TeamQuery;
import com.user.partner.service.TeamService;
import com.user.util.common.B;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ice
 * @date 2022/8/22 16:02
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("addTeam")
    public B<Boolean> addTeam(@RequestBody Team team) {
        if (team == null) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        boolean save = teamService.save(team);
        if (!save) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "保存失败");
        }
        return B.ok();
    }

    @GetMapping("/delete")
    public B<Boolean> deleteById(@RequestParam("id") long id) {
        if (id <= 0) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        boolean b = teamService.removeById(id);
        if (!b) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "删除失败");
        }
        return B.ok();
    }

    @PostMapping("update")
    public B<Boolean> update(@RequestBody Team team) {
        if (team == null) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        boolean save = teamService.updateById(team);
        if (!save) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "跟新失败");
        }
        return B.ok();
    }

    @GetMapping("/get")
    public B<Team> getTeamById(@RequestParam("id") long id) {
        if (id <= 0) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        return B.ok(team);
    }

    @GetMapping("/list")
    public B<Page<Team>> getTeamList(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        QueryWrapper<Team> wrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        Page<Team> resultPage = teamService.page(teamPage, wrapper);
        return B.ok(resultPage);
    }
}
