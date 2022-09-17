package com.user.partner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.user.model.domain.UserTeam;
import com.user.partner.service.UserTeamService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ice
 * @date 2022/9/10 17:32
 */
@SpringBootTest
public class ServiceTest {
    @Resource
    private UserTeamService userTeamService;

    @Test
    void test1() {
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        String id = "1536633983511666690";
        wrapper.eq("user_id",id);
        wrapper.groupBy("user_id");
        List<UserTeam> list = userTeamService.list(wrapper);
        list.forEach(System.out::println);
    }
}
