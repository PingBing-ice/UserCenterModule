package com.user.usercenter;

import com.user.model.domain.User;
import com.user.usercenter.mapper.UserMapper;
import com.user.usercenter.service.IUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * @author ice
 * @date 2022/6/14 10:57
 */
@SpringBootTest
public class TestSql {
    @Resource
    private UserMapper userMapper;
    @Resource
    private IUserService userService;
    @Test
    public void test1() {

        User one = userService.getOne(null);
        System.out.println(one);
        boolean b = userService.removeById(one);
        Assertions.assertTrue(b);
    }

    @Test
    public void TestPassword() {
        String passwordMD = DigestUtils.md5DigestAsHex(("SALT" + "password").getBytes());
        System.out.println(passwordMD);

    }


}
