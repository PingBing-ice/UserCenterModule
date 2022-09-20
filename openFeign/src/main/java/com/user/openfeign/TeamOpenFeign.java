package com.user.openfeign;

import com.user.model.domain.Team;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author ice
 * @date 2022/9/12 19:17
 */
@FeignClient("user-partner")
public interface TeamOpenFeign {

    // /partner
    @GetMapping("/partner/userTeam/getUserTeamListById")
    List<String> getUserTeamListById(@RequestParam("teamId") String teamId,@RequestParam("userId")String userId);

    @GetMapping("/partner/userTeam/getTeamByUserId")
    Team getTeamByTeamUser(@RequestParam("teamId") String teamId, @RequestParam("userId") String userId);

    @PostMapping("/partner/userTeam/updateTeamByTeam")
    public boolean updateTeamByTeam(@RequestBody Team team);
}
