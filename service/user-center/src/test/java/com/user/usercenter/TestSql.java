package com.user.usercenter;

import com.user.util.utils.ThreadUtil;

import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ExecutionException;

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

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println(System.currentTimeMillis() + "===================");
            String a = "a";
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                System.out.println(System.currentTimeMillis() + "===000000000000000000000000000000=="+a);

            }, ThreadUtil.getThreadPool());
            System.out.println("===========结束=========" + System.currentTimeMillis() + "==" );

            completableFuture.join();

        }

    }

    public static boolean is() {





        return false;
    }

    public  static void is2(String a) {


    }
}
