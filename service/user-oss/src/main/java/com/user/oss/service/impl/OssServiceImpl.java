package com.user.oss.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.user.model.constant.RedisKey;
import com.user.model.domain.Team;
import com.user.model.domain.User;
import com.user.openfeign.TeamOpenFeign;
import com.user.openfeign.UserOpenFeign;
import com.user.oss.service.OssService;
import com.user.oss.util.ConstantPropertiesUtils;
import com.user.oss.util.ResponseEmail;
import com.user.rabbitmq.config.mq.MqClient;
import com.user.rabbitmq.config.mq.RabbitService;
import com.user.util.common.ErrorCode;
import com.user.util.exception.GlobalException;
import com.user.model.utils.IpUtilSealUp;
import com.user.util.utils.IpUtils;
import com.user.util.utils.RandomUtil;
import com.user.util.utils.TimeUtils;
import com.user.util.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private UserOpenFeign userOpenFeign;
    @Resource
    private TeamOpenFeign teamOpenFeign;

    @Resource
    private RedissonClient redissonClient;


    /**
     * 用户头像的上传
     *
     * @param file 上传的文件
     * @param loginUser 登录的用户
     * @return 返回之
     */
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
                String redisKey = RedisKey.ossAvatarUserRedisKey + userId;
                String url = getUrl(redisKey, file);
                User user = new User();
                user.setId(userId);
                user.setAvatarUrl(url);
                rabbitService.sendMessage(MqClient.DIRECT_EXCHANGE, MqClient.OSS_KEY, user);
                Integer integer = TimeUtils.getRemainSecondsOneDay(new Date());
                stringRedisTemplate.opsForValue().set(redisKey, new Date().toString(), integer, TimeUnit.SECONDS);
                // 删除掉主页的用户
                rabbitService.sendMessage(MqClient.DIRECT_EXCHANGE, MqClient.REMOVE_REDIS_KEY, RedisKey.redisIndexKey);
                return url;
            }
        } catch (InterruptedException e) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "系统错误");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return null;
    }

    @Override
    public String upFileByTeam(MultipartFile file, User loginUser, String teamID) {
        RLock lock = redissonClient.getLock(RedisKey.redisFileByTeamAvatarLock);
        try {
            if (lock.tryLock(0, 3000, TimeUnit.MILLISECONDS)) {
                if (file == null || !StringUtils.hasText(teamID)) {
                    throw new GlobalException(ErrorCode.NULL_ERROR);
                }
                String userId = loginUser.getId();
                Team team = teamOpenFeign.getTeamByTeamUser(teamID, userId);
                if (team == null) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "队伍不存在...");
                }
                String teamUserId = team.getUserId();
                if (!userId.equals(teamUserId)) {
                    throw new GlobalException(ErrorCode.NO_AUTH, "权限不足...");
                }
                String redisKey = RedisKey.ossAvatarTeamRedisKey + teamID;
                String url = getUrl(redisKey, file);
                team.setAvatarUrl(url);
                boolean teamByTeam = teamOpenFeign.updateTeamByTeam(team);
                if (!teamByTeam) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "上传错误...");
                }
                Integer integer = TimeUtils.getRemainSecondsOneDay(new Date());
                stringRedisTemplate.opsForValue().set(redisKey, new Date().toString(), integer, TimeUnit.SECONDS);
                return url;
            }
        } catch (InterruptedException e) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "系统错误");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return null;
    }

    public String getUrl(String redisKey, MultipartFile file) {
        String key = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.hasText(key)) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "今日上限...");
        }
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtils.END_POINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        // 返回客服端的原始名字
        String originalFilename = IdUtil.simpleUUID() + file.getOriginalFilename();
        String objectName = "user/" + new DateTime().toString("yyyy/MM/dd") + "/" + originalFilename;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            InputStream inputStream = file.getInputStream();
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, inputStream);
            return "https://" + bucketName + "." + endpoint + "/" + objectName;
        } catch (Exception oe) {
            log.error(oe.getMessage());
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "上传失败");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 忘记密码
     *
     * @param responseEmail responseEmail
     * @param request 用户信息
     * @return true
     */
    @Override
    public boolean sendForgetEMail(ResponseEmail responseEmail, HttpServletRequest request) {
        RLock lock = redissonClient.getLock(RedisKey.redisFileByForgetLock);
        try {
            if (lock.tryLock(0, 3000, TimeUnit.MILLISECONDS)) {
                // 获取真实ip
                ipEmailUtil(request);
                if (responseEmail == null) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "请输入邮箱");
                }
                String email = responseEmail.getEmail();
                String userAccount = responseEmail.getUserAccount();
                if (!StringUtils.hasText(email)) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "请输入邮箱");
                }
                if (!StringUtils.hasText(userAccount)) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "请输入账号");
                }
                String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

                Matcher matcher = Pattern.compile(pattern).matcher(email);
                if (!matcher.matches()) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "请输入正确邮箱");
                }
                User user = userOpenFeign.forgetUserEmail(email);

                if (user == null) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "该邮箱没有注册过");
                }
                String userUserAccount = user.getUserAccount();
                if (!userAccount.equals(userUserAccount)) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "请输入该邮箱绑定的账号");
                }

                String code = RandomUtil.getRandomFour();
                String[] split = email.split("@");
                String name = split[0];
                boolean sendQQEmail = sendQQEmail(email, code, name);
                if (!sendQQEmail) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "发送失败请重试");
                }
                String redisKey = RedisKey.redisForgetCode + email;
                try {
                    stringRedisTemplate.opsForValue().set(redisKey, code, 60, TimeUnit.SECONDS);
                } catch (Exception e) {
                    return false;
                }
            }
        } catch (InterruptedException e) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "系统错误");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * 发送注册邮件
     *
     * @param responseEmail 接受的邮件
     * @return 返回Boolean
     */
    @Override
    public boolean sendEMail(ResponseEmail responseEmail, HttpServletRequest request) {
        RLock lock = redissonClient.getLock(RedisKey.redisFileByRegisterLock);
        try {
            if (lock.tryLock(0, 3000, TimeUnit.MILLISECONDS)) {
                // 获取真实ip
                ipEmailUtil(request);

                if (responseEmail == null) {
                    throw new GlobalException(ErrorCode.NULL_ERROR, "请输入邮箱");
                }
                String email = getEmail(responseEmail);
                if (userOpenFeign.seeUserEmail(email)) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "注册邮箱重复");
                }
                String code = getCode(email);
                String redisKey = RedisKey.redisRegisterCode + email;
                try {
                    stringRedisTemplate.opsForValue().set(redisKey, code, 60, TimeUnit.SECONDS);
                } catch (Exception e) {
                    return false;
                }
            }
        } catch (InterruptedException e) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "系统错误");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return true;
    }

    /**
     * 发送绑定验证码
     *
     * @param responseEmail responseEmail
     * @param request s
     * @return s
     */
    @Override
    public boolean sendBinDingEMail(ResponseEmail responseEmail, HttpServletRequest request) {
        RLock lock = redissonClient.getLock(RedisKey.redisFileByBingDingLock);
        try {
            if (lock.tryLock(0, 3000, TimeUnit.MILLISECONDS)) {
                // 获取真实ip

                ipEmailUtil(request);
                User user = UserUtils.getLoginUser(request);
                String email = getEmail(responseEmail);
                String userEmail = user.getEmail();
                if (StringUtils.hasText(userEmail)) {
                    if (!userEmail.equals(email)) {
                        throw new GlobalException(ErrorCode.PARAMS_ERROR, "");
                    }
                }
                if (userOpenFeign.seeUserEmail(email)) {
                    throw new GlobalException(ErrorCode.PARAMS_ERROR, "该邮箱已被注册");
                }

                String code = getCode(email);
                String redisKey = RedisKey.redisFileByBingDingKey + email;
                try {
                    stringRedisTemplate.opsForValue().set(redisKey, code, 60, TimeUnit.SECONDS);
                } catch (Exception e) {
                    return false;
                }
            }
        } catch (InterruptedException e) {
            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "系统错误");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return true;
    }

    private String getCode(String email) {
        String code = RandomUtil.getRandomSix();
        String[] split = email.split("@");
        String name = split[0];
        boolean sendQQEmail = sendQQEmail(email, code, name);
        if (!sendQQEmail) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "发送失败请重试");
        }
        return code;
    }

    private String getEmail(ResponseEmail responseEmail) {
        String email = responseEmail.getEmail();
        if (!StringUtils.hasText(email)) {
            throw new GlobalException(ErrorCode.NULL_ERROR, "请输入邮箱");
        }
        String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

        Matcher matcher = Pattern.compile(pattern).matcher(email);
        if (!matcher.matches()) {
            throw new GlobalException(ErrorCode.PARAMS_ERROR, "请输入正确邮箱");
        }
        return email;
    }

    /**
     * 发送邮件(参数自己根据自己的需求来修改，发送短信验证码)
     *
     * @param receives 接收人的邮箱
     * @param code     验证码
     * @param name     收件人的姓名
     * @return 是否成功
     */
    public boolean sendQQEmail(String receives, String code, String name) {
        String from_email = ConstantPropertiesUtils.EMAIL;
        String pwd = ConstantPropertiesUtils.EMAILPASSWORD;
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");     //使用smpt的邮件传输协议
        props.setProperty("mail.smtp.host", "smtp.qq.com");       //主机地址
        props.setProperty("mail.smtp.auth", "true");      //授权通过

        Session session = Session.getInstance(props);     //通过我们的这些配置，得到一个会话程序

        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from_email));     //设置发件人
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receives, "用户", "utf-8"));      //设置收件人
            message.setSubject("验证码", "utf-8");      //设置主题
            message.setSentDate(new Date());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            // 模板
            String str = "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body><p style='font-size: 20px;font-weight:bold;'>尊敬的：" + name + "，您好！</p>"
                    + "<p style='text-indent:2em; font-size: 20px;'>欢迎注册伙伴匹配系统，您本次的验证码是 "
                    + "<span style='font-size:30px;font-weight:bold;color:red'>" + code + "</span>，1分钟之内有效，请尽快使用！</p>"
                    + "<p style='text-align:right; padding-right: 20px;'"
                    + "<a href='http://www.hyycinfo.com' style='font-size: 18px'></a></p>"
                    + "<span style='font-size: 18px; float:right; margin-right: 60px;'>" + sdf.format(new Date()) + "</span></body></html>";

            Multipart mul = new MimeMultipart();  //新建一个MimeMultipart对象来存放多个BodyPart对象
            BodyPart mdp = new MimeBodyPart();  //新建一个存放信件内容的BodyPart对象
            mdp.setContent(str, "text/html;charset=utf-8");
            mul.addBodyPart(mdp);  //将含有信件内容的BodyPart加入到MimeMultipart对象中
            message.setContent(mul); //把mul作为消息内容


            message.saveChanges();

            //创建一个传输对象
            Transport transport = session.getTransport("smtp");

            //建立与服务器的链接  465端口是 SSL传输
            transport.connect("smtp.qq.com", 587, from_email, pwd);

            //发送邮件
            transport.sendMessage(message, message.getAllRecipients());

            //关闭邮件传输
            transport.close();

        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    private void ipEmailUtil(HttpServletRequest request) {
        String ipAddress = IpUtils.getIpAddress(request);

        String num = stringRedisTemplate.opsForValue().get(ipAddress);
        if (StringUtils.hasText(num)) {
            int max = Integer.parseInt(num);
            // 一天的次数过多
            if (max >= 20) {
                IpUtilSealUp.addIpList(ipAddress);
                throw new GlobalException(ErrorCode.PARAMS_ERROR);
            }
            stringRedisTemplate.opsForValue().increment(ipAddress);
        } else {
            stringRedisTemplate.opsForValue().set(ipAddress, "1", TimeUtils.getRemainSecondsOneDay(new Date()),
                    TimeUnit.SECONDS);
        }
    }

}
