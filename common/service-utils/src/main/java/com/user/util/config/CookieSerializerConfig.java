package com.user.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author ice
 * @date 2022/8/23 10:58
 */
@Configuration
public class CookieSerializerConfig{

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // cookie 的名字
        cookieSerializer.setCookieName("SESSION");
        // 允许跨子域共享cookie。默认是使用当前域
        cookieSerializer.setDomainName("localhost");
        // cookie的路径
        cookieSerializer.setCookiePath("/");

        return cookieSerializer;
    }
}
