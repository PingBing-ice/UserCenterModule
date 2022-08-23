package com.user.partner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author ice
 * @date 2022/8/22 19:49
 */
@SpringBootApplication
@ComponentScan("com.user")
@EnableDiscoveryClient
@EnableRedisHttpSession
@MapperScan("com.user.partner.mapper")
@EnableFeignClients("com.user")
public class ServicePartnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePartnerApplication.class, args);
    }
}
