package com.user.usercenter;
import java.time.LocalDateTime;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.google.gson.Gson;
import com.user.model.domain.User;
import com.user.usercenter.service.IUserService;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.util.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author ice
 * @date 2022/7/13 11:33
 */
//@SpringBootTest
public class UserServiceTest {
    //    @Autowired
//    private IUserService userService;
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//    @Test
//    public void TestSearchUserTag() {
//        redisTemplate.opsForValue().set("1",2,3000, TimeUnit.MILLISECONDS);
//        Object o = redisTemplate.opsForValue().get("1");
//        System.out.println(o);
//    }
    public static void main(String[] args) {
        User user = new User();
        user.setId("123456");
        String jwtToken = JwtUtils.getJwtToken(user);
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey("ukc8BDbgUDaY6pZFfWus2jZWLPHO").parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        Gson gson = new Gson();

        String o = (String) claims.get("user");
        User fromJson = gson.fromJson(o, User.class);
        System.out.println(fromJson);
    }


}
