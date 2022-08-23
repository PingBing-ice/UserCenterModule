package com.user.usercenter;

import com.user.usercenter.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author ice
 * @date 2022/7/13 11:33
 */
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Test
    public void TestSearchUserTag() {
        redisTemplate.opsForValue().set("1",2,3000, TimeUnit.MILLISECONDS);
        Object o = redisTemplate.opsForValue().get("1");
        System.out.println(o);
    }
}
