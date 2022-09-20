package com.user.usercenter;

import java.io.FileInputStream;
import java.nio.channels.FileChannel;

/**
 * @author ice
 * @date 2022/6/14 10:57
 */

public class TestSql {
//    @Resource
//    private UserMapper userMapper;
//    @Resource
//    private IUserService userService;
//    @Test
//    public void test1() {
//
//        User one = userService.getOne(null);
//        System.out.println(one);
//        boolean b = userService.removeById(one);
//        Assertions.assertTrue(b);
//    }
//
//    @Test
//    public void TestPassword() {
//        String passwordMD = DigestUtils.md5DigestAsHex(("SALT" + "password").getBytes());
//        System.out.println(passwordMD);
//
//    }

    public static void main(String[] args) throws Exception{
        FileChannel fileChannel = new FileInputStream("").getChannel();

        int i = Integer.parseInt("1");
    }
}
