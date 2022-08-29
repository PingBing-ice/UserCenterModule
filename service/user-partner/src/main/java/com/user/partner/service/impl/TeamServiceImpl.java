package com.user.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user.model.domain.Team;
import com.user.model.domain.User;
import com.user.model.domain.UserTeam;
import com.user.model.domain.vo.TeamUserVo;
import com.user.model.domain.vo.UserVo;
import com.user.model.dto.TeamQuery;
import com.user.model.enums.TeamStatusEnum;
import com.user.model.request.TeamJoinRequest;
import com.user.model.request.TeamUpdateRequest;
import com.user.partner.mapper.TeamMapper;
import com.user.partner.service.TeamService;
import com.user.partner.service.UserTeamService;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.util.openfeign.UserOpenFeign;
import com.user.util.utils.MD5;
import com.user.util.utils.UserUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author BING
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2022-08-22 15:45:11
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Autowired
    private RedissonClient redissonClient;

    @Resource
    private UserTeamService userTeamService;

    @Autowired
    private UserOpenFeign userOpenFeign;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        RLock lock = redissonClient.getLock("user:addTeam:key");
        try {
            if (lock.tryLock(0, 3000, TimeUnit.MILLISECONDS)) {
                //        校验信息
                //队伍人数 > 1 且 <= 20
                long maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0L);
                if (maxNum < 1 || maxNum >= 20) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
                }
                //队伍标题 <= 20
                String teamName = team.getName();
                if (!StringUtils.hasText(teamName) && teamName.length() >= 20) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
                }
                String teamDescription = team.getDescription();
                //描述 <= 512
                if (teamDescription.length() >= 512) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "队伍描述不满足要求");
                }
                //status 是否公开（int）不传默认为 0（公开）
                int status = Optional.ofNullable(team.getStatus()).orElse(0);
                String password = team.getPassword();
                TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusByValue(status);
                //如果 status 是加密状态，一定要有密码，且密码 <= 32
                if (TeamStatusEnum.ENCRYPTION.equals(statusEnum) && (!StringUtils.hasText(password) || password.length() > 32)) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "队伍密码不满足要求");
                }
                //超时时间 > 当前时间
                Date expireTime = team.getExpireTime();
                if (expireTime == null || new Date().after(expireTime)) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
                }
                //校验用户最多创建 5 个队伍
                String userId = loginUser.getId();
                QueryWrapper<Team> wrapper = new QueryWrapper<>();
                wrapper.eq("user_id", userId);
                long count = this.count(wrapper);
                if (count > 5) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
                }
                //        插入队伍信息到队伍表
                team.setId(null);
                team.setUserId(Long.parseLong(userId));
                boolean save = this.save(team);

                Long teamId = team.getId();
                if (!save || teamId == null) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "插入失败");
                }
                //插入用户 => 队伍关系到关系表
                UserTeam userTeam = new UserTeam();
                userTeam.setName(team.getName());
                userTeam.setUserId(Long.parseLong(userId));
                userTeam.setTeamId(teamId);
                userTeam.setJoinTime(new Date());
                boolean result = userTeamService.save(userTeam);
                if (!result) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "插入失败");
                }
                return teamId;
            }
            return -1;
        } catch (InterruptedException e) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "加锁失败");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    /**
     * 根据id删除队伍
     *
     * @param id      队伍的id
     * @param request 登录用户
     * @return boolean
     */
    @Override
    @Transactional
    public boolean deleteById(long id, HttpServletRequest request) {
        User loginUser = UserUtils.getLoginUser(request);
        String userId = loginUser.getId();
        if (!StringUtils.hasText(userId)) {
            throw new GlobalException(ErrorCode.NULL_ERROR, "未登录");
        }
        Team team = this.getById(id);
        if (team == null || team.getId() < 0) {
            throw new GlobalException(ErrorCode.NULL_ERROR, "没有该队伍");
        }
        Long teamUserId = team.getUserId();
        if (!userId.equals(teamUserId.toString()) || !UserUtils.isAdmin(request)) {
            throw new GlobalException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", team.getId());
        userTeamService.remove(userTeamQueryWrapper);
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("user_id", userId).and(wrapper -> wrapper.eq("id", id));
        return this.remove(teamQueryWrapper);
    }

    @Override
    public List<TeamUserVo> getTeamList(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        wrapper.and(wr -> wr.gt(true, "expire_time", new Date()).or().isNotNull("expire_time"));
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                wrapper.eq("id", id);
            }
            String searchTxt = teamQuery.getSearchTxt();
            if (StringUtils.hasText(searchTxt)) {
                wrapper.and(wq -> wq.like("name", searchTxt).or().like("description", searchTxt));
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                wrapper.eq("max_num", maxNum);

            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                wrapper.eq("user_id", userId);
            }
            Integer status = teamQuery.getStatus();

            TeamStatusEnum teamStatusByValue = TeamStatusEnum.getTeamStatusByValue(status);
            if (teamStatusByValue == null) {
                teamStatusByValue = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !teamStatusByValue.equals(TeamStatusEnum.PUBLIC)) {
                throw new GlobalException(ErrorCode.NO_AUTH);
            }
            wrapper.eq("status", teamStatusByValue.getValue());

        }


        /*
        select *
               from team t
         left join user_team ut on t.id = ut.team_id
         left join user u on t.user_id = u.id
         */
        List<Team> list = this.list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<TeamUserVo> teamUserVos = new ArrayList<>();
        for (Team team : list) {
            Long teamId = team.getId();
            QueryWrapper<UserTeam> teamQueryWrapper = new QueryWrapper<>();
            teamQueryWrapper.eq("team_id", teamId);
            List<UserTeam> userTeams = userTeamService.list(teamQueryWrapper);

            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);

            if (!CollectionUtils.isEmpty(userTeams)) {
                for (UserTeam userTeam : userTeams) {
                    Long userId = userTeam.getUserId();
                    User userById = userOpenFeign.getUserById(userId.toString());
                    if (userById != null) {
                        UserVo userVo = new UserVo();
                        BeanUtils.copyProperties(userById, userVo);
                        teamUserVo.getUserVo().add(userVo);
                    }

                }
            }
            teamUserVos.add(teamUserVo);

//            Long userId = team.getUserId();
//            if (userId == null) {
//                continue;
//            }
//            User user = userOpenFeign.getUserById(userId.toString());
//
//            User safetyUser = UserUtils.getSafetyUser(user);
//            // 返回脱敏的用户消息
//            TeamUserVo teamUserVo = new TeamUserVo();
//            if (safetyUser != null) {
//                UserVo userVo = new UserVo();
//                BeanUtils.copyProperties(safetyUser, userVo);
//                BeanUtils.copyProperties(team, teamUserVo);
//                teamUserVo.setUserVo(userVo);
//            }
//            teamUserVos.add(teamUserVo);
        }
        return teamUserVos;
    }

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param request
     * @return
     */
    @Override
    public boolean addUserTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        RLock lock = redissonClient.getLock("user::team::addUserTeam");
        try {
            if (lock.tryLock(3000, 3000, TimeUnit.MILLISECONDS)) {
                User loginUser = UserUtils.getLoginUser(request);
                if (teamJoinRequest == null) {
                    throw new GlobalException(ErrorCode.NULL_ERROR);
                }
                String teamId = teamJoinRequest.getTeamId();
                if (!StringUtils.hasText(teamId)) {
                    throw new GlobalException(ErrorCode.NULL_ERROR);
                }
                Team team = this.getById(teamId);
                if (team == null) {
                    throw new GlobalException(ErrorCode.NULL_ERROR);
                }
                // 判断队伍的过期
                Date expireTime = team.getExpireTime();
                if (expireTime == null || expireTime.before(new Date())) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "队伍已过期");
                }

                // 判断队伍的权限
                Integer status = team.getStatus();
                TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusByValue(status);
                if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "禁止加入私有队伍");
                }
                String password = teamJoinRequest.getPassword();
                if (TeamStatusEnum.ENCRYPTION.equals(statusEnum)) {
                    if (!StringUtils.hasText(password) || !team.getPassword().equals(MD5.getTeamMD5(password))) {
                        throw new GlobalException(ErrorCode.PARAMS_ERROR, "密码错误");
                    }
                }
                // 用户加入队伍不超过 5 个
                QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
                wrapper.eq("user_id", loginUser.getId());
                long count = userTeamService.count(wrapper);
                if (count > 5) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "你最多加入 5 个队伍");
                }

                // 队伍已满
                wrapper = new QueryWrapper<>();
                wrapper.eq("team_id", teamId);
                count = userTeamService.count(wrapper);
                if (count > team.getMaxNum()) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "队伍已满");
                }
                // 不能加入已经加入的队伍
                wrapper = new QueryWrapper<>();
                wrapper.eq("team_id", teamId);
                wrapper.eq("user_id", loginUser.getId());
                count = userTeamService.count(wrapper);
                if (count > 0) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "用户以加入队伍");
                }

                // 保存
                UserTeam userTeam = new UserTeam();
                userTeam.setName(team.getName());
                userTeam.setUserId(Long.parseLong(loginUser.getId()));
                userTeam.setTeamId(team.getId());
                userTeam.setJoinTime(new Date());
                return userTeamService.save(userTeam);
            }
        } catch (InterruptedException e) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "加锁失败");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }


    @Override
    @Transactional
    public void updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        User loginUser = UserUtils.getLoginUser(request);
        String id = teamUpdateRequest.getId();
        if (!StringUtils.hasText(id)) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        Team team = this.getById(id);
        if (team == null) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "队伍为空");
        }
        if (!loginUser.getId().equals(team.getUserId().toString()) && !UserUtils.isAdmin(request)) {
            throw new GlobalException(ErrorCode.NO_AUTH);
        }
        Integer status = teamUpdateRequest.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusByValue(status);
        String password = teamUpdateRequest.getPassword();
        if (statusEnum.equals(TeamStatusEnum.ENCRYPTION)) {
            if (!StringUtils.hasText(password)) {
                throw new GlobalException(ErrorCode.NULL_ERROR, "请设置密码");
            }
        }
        team = new Team();
        team.setId(Long.parseLong(teamUpdateRequest.getId()));
        BeanUtils.copyProperties(teamUpdateRequest, team);
        team.setPassword(MD5.getTeamMD5(team.getPassword()));
        boolean b = this.updateById(team);
        if (!b) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "跟新失败");
        }
    }
    // 退出退伍
    @Override
    public boolean quitTeam(String teamId, HttpServletRequest request) {
        if (!StringUtils.hasText(teamId)) {
            throw new GlobalException(ErrorCode.NULL_ERROR);
        }
        User loginUser = UserUtils.getLoginUser(request);

        Team team = this.getById(teamId);
        if (team == null || team.getId() < 0) {
            throw new GlobalException(ErrorCode.NULL_ERROR, "没有该队伍");
        }
        Long userId = team.getUserId();
        boolean admin = UserUtils.isAdmin(request);
        String loginUserId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", loginUserId);
        userTeamQueryWrapper.eq("team_id", teamId);
        UserTeam userTeam = userTeamService.getOne(userTeamQueryWrapper);
        if (userTeam ==null) {
            throw new GlobalException(ErrorCode.NULL_ERROR, "不在队伍中");
        }
        Long teamUserId = userTeam.getUserId();
        if (!loginUserId.equals(userId.toString()) || !admin || !loginUserId.equals(teamUserId.toString())) {
            throw new GlobalException(ErrorCode.NO_AUTH);
        }
        // TODO 如果是创建者进行退位
        return userTeamService.removeById(userTeam.getId());
    }
}




