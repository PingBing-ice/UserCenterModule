package com.user.usercenter.job;

import com.user.usercenter.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ice
 * @date 2022/8/19 15:15
 */
@Component
@Slf4j
public class ProCacheJob {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private IUserService userService;
    @Resource
    private RedissonClient redissonClient;

    private final List<String> mainUserList = Arrays.asList("1536633983511666690", "1539235072924192769");

    @Scheduled(cron = "0 4 0 1/1 * ?")
    public void setIndexRedisMap() {
        RLock lock = redissonClient.getLock("user:recommend:key");
        try {
            if (lock.tryLock(0, 3000, TimeUnit.MILLISECONDS)) {

                for (String userId : mainUserList) {
                    String redisKey = String.format("user:recommend:%s", userId);
                    Map<String, Object> map = userService.selectPageIndexList(1, 500);
                    try {
                        redisTemplate.opsForValue().set(redisKey, map, 1, TimeUnit.DAYS);
                    } catch (Exception e) {
                        log.error("缓存预热失败 => " + e.getMessage());
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("缓存预热失败 ==> " + e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}