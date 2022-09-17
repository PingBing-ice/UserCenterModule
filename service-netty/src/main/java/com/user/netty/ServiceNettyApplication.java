package com.user.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ice
 * @date 2022/8/22 19:37
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.user")
@EnableFeignClients("com.user")
@EnableDiscoveryClient
public class ServiceNettyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceNettyApplication.class, args);
    }
}
