package com.user.usercenter;

/**
 * @author ice
 * @date 2022/7/21 18:56
 */
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@RunWith(SpringRunner.class)
public class InsertUserTest {
//    @Autowired
//    private IUserService userService;
//
//
//
//    @Test
//    public void doInsertUser() {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        final int INSERT_NUM = 1000000;
//        List<User> list = new ArrayList<>();
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUsername("假用户" + i);
//            user.setUserAccount("假ice");
//            user.setGender("男");
//            user.setPassword("12345678");
//            user.setTags("" + i);
//            user.setProfile("假");
//            user.setAvatarUrl("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif?imageView2/1/w/80/h/80");
//            user.setTel("110");
//            user.setEmail("111@qq.com");
//            user.setPlanetCode("1111");
////            userMapper.insert(user);
//            list.add(user);
//        }
//        userService.saveBatch(list, 10000);
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }
//
//    @Test
//    public void doConcurrencyInsertUser() {
////        ExecutorService executor = new ThreadPoolExecutor(100
////                , 200
////                , 100L
////                , TimeUnit.SECONDS, new ArrayBlockingQueue<>(3),
////                Executors.defaultThreadFactory(),
////                new ThreadPoolExecutor.AbortPolicy());
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        final int INSERT_NUM = 1000000;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        int j = 0;
//        for (int i = 0; i < 40; i++) {
//            CopyOnWriteArrayList<User> list = new CopyOnWriteArrayList<>();
//            while (true) {
//                j++;
//                User user = new User();
//                user.setUsername("假用户" + i);
//                user.setUserAccount("假ice"+i);
//                user.setGender("男");
//                user.setPassword("12345678");
//                user.setTags("[\"java\"]");
//                user.setProfile("假");
//                user.setAvatarUrl("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif?imageView2/1/w/80/h/80");
//                user.setTel("110");
//                user.setEmail("111@qq.com");
//                user.setPlanetCode("1111"+i);
//
////            userMapper.insert(user);
//                list.add(user);
//                if (j % 25000 == 0) {
//                    break;
//                }
//            }
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                userService.saveBatch(list, 1000);
//            }, ThreadUtil.getThreadPool());
//
//            futureList.add(future);
//        }
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//        // 177523 62780
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }



}
