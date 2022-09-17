package com.user.partner.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.user.model.domain.Team;
import com.user.model.domain.User;
import com.user.model.domain.vo.TeamUserVo;
import com.user.model.dto.TeamQuery;
import com.user.model.request.TeamJoinRequest;
import com.user.model.request.TeamUpdateRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author BING
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2022-08-22 15:45:11
*/
public interface TeamService extends IService<Team> {

    /**
     * 保存用户
     * @param team
     * @param loginUser
     * @return
     */
    String addTeam(Team team, User loginUser);

    /**
     * 根据id删除队伍
     * @param id
     * @param request
     * @return
     */
    boolean deleteById(long id,HttpServletRequest request);

    /**
     * 查询队伍列表
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVo> getTeamList(TeamQuery teamQuery, boolean isAdmin);


    /**
     * 添加队伍
     * @param teamJoinRequest
     * @param request
     * @return
     */
    boolean addUserTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request);

    /**
     *  修改队伍
     * @param teamUpdateRequest
     * @param request
     */
    void updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    /**
     * 退出队伍
     * @param teamId 队伍的id
     * @param request  登录用户
     * @return
     */
    boolean quitTeam(String teamId, HttpServletRequest request);

    /**
     * 查看用户加入的队伍
     * @param request 1
     * @return 200
     */
    List<TeamUserVo> getJoinTeamList(HttpServletRequest request);

    /**
     * 根据id获取信息
     * @param id
     * @return
     */
    TeamUserVo getByTeamId(String id);
}
