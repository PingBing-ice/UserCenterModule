package com.user.usercenter;

import com.user.model.domain.User;

import com.user.usercenter.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author ice
 * @date 2022/7/21 18:56
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class InsertUserTest {
    @Autowired
    private IUserService userService;

    private final ExecutorService executor = new ThreadPoolExecutor(60, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    @Test
    public void doInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 1000000;
        List<User> list = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户" + i);
            user.setUserAccount("假ice");
            user.setGender("男");
            user.setPassword("12345678");
            user.setTags("" + i);
            user.setProfile("假");
            user.setAvatarUrl("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif?imageView2/1/w/80/h/80");
            user.setTel("110");
            user.setEmail("111@qq.com");
            user.setPlanetCode("1111");
//            userMapper.insert(user);
            list.add(user);
        }
        userService.saveBatch(list, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void doConcurrencyInsertUser() {
//        ExecutorService executor = new ThreadPoolExecutor(100
//                , 200
//                , 100L
//                , TimeUnit.SECONDS, new ArrayBlockingQueue<>(3),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.AbortPolicy());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 1000000;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < 40; i++) {
            CopyOnWriteArrayList<User> list = new CopyOnWriteArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("假用户" + i);
                user.setUserAccount("假ice");
                user.setGender("男");
                user.setPassword("12345678");
                user.setTags("[\"java\"]");
                user.setProfile("假");
                user.setAvatarUrl("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif?imageView2/1/w/80/h/80");
                user.setTel("110");
                user.setEmail("111@qq.com");
                user.setPlanetCode("1111");

//            userMapper.insert(user);
                list.add(user);
                if (j % 25000 == 0) {
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(list, 1000);
                System.out.println("threadName :" + Thread.currentThread().getName());
            },executor);

            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 177523 62780
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }



}
