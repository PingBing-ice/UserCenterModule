package com.user.usercenter.job;

import com.user.model.constant.RedisKey;
import com.user.usercenter.service.IUserService;
import com.user.util.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
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

    // 0 0 0 * * ?
    @Scheduled(cron = "0 0 0 * * ?")
    public void setIndexRedisMap() {
        RLock lock = redissonClient.getLock("cron:user:recommend:index:key");
        try {
            if (lock.tryLock(0, 3000, TimeUnit.MILLISECONDS)) {
                Map<String, Object> map = userService.selectPageIndexList(1, 500);
                try {
                    redisTemplate.opsForValue().set(RedisKey.redisIndexKey, map, TimeUtils.getRemainSecondsOneDay(new Date()), TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("缓存预热失败 => " + e.getMessage());
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