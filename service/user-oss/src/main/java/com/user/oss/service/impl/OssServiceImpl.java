package com.user.oss.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.user.model.constant.RedisKey;
import com.user.model.domain.User;
import com.user.oss.service.OssService;
import com.user.rabbitmq.config.mq.MqClient;
import com.user.rabbitmq.config.mq.RabbitService;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.util.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ice
 * @date 2022/9/17 12:48
 */
@Service
@Slf4j
public class OssServiceImpl implements OssService {
    @Resource
    private RabbitService rabbitService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;
    // 头像上传
    @Override
    public String upload(MultipartFile file, User loginUser) {
        RLock lock = redissonClient.getLock(RedisKey.redisFileAvatarLock);
        try {
            if (lock.tryLock(0, 3000, TimeUnit.MILLISECONDS)) {
                if (file == null) {
                    throw new GlobalException(ErrorCode.NULL_ERROR);
                }
                // 判断用户是否上传过
                String userId = loginUser.getId();
                String redisKey = RedisKey.ossAvatarRedisKey + userId;
                String key = stringRedisTemplate.opsForValue().get(redisKey);
                if (StringUtils.hasText(key)) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "今日上限...");
                }
                // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
                String endpoint = "oss-cn-hangzhou.aliyuncs.com";
                // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
                String accessKeyId = "LTAI5tANtaA1KTAdsZr9b3Ai";
                String accessKeySecret = "lBIkbIJb94EcknMRIlFvRL9YkGIdSV";
                // 填写Bucket名称，例如examplebucket。
                String bucketName = "bing-edu";
                // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
                // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
                // 返回客服端的原始名字
                String originalFilename = IdUtil.simpleUUID()+file.getOriginalFilename();
                String objectName = "user/"+new DateTime().toString("yyyy/MM/dd") +"/"+originalFilename;

                // 创建OSSClient实例。
                OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

                try {
                    InputStream inputStream = file.getInputStream();
                    // 创建PutObject请求。
                    ossClient.putObject(bucketName, objectName, inputStream);
                    String url ="https://" + bucketName + "." + endpoint + "/" + objectName;
                    User user = new User();
                    user.setId(userId);
                    user.setAvatarUrl(url);
                    rabbitService.sendMessage(MqClient.NETTY_EXCHANGE,MqClient.OSS_KEY,user);
                    Integer integer = TimeUtils.getRemainSecondsOneDay(new Date());
                    stringRedisTemplate.opsForValue().set(redisKey,new Date().toString(),integer, TimeUnit.SECONDS);
                    return url;
                } catch (Exception oe) {
                    log.error(oe.getMessage());
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "上传失败");
                } finally {
                    if (ossClient != null) {
                        ossClient.shutdown();
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "加锁失败");
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return null;
    }
}
