package com.user.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ice
 * @date 2022/9/13 10:39
 */
@Component
@Slf4j
public class MyLogGetWayFilter implements GlobalFilter, Ordered {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI URIPath = request.getURI();
        String path = request.getPath().value();
        String method = request.getMethodValue();
        log.info("***********************请求信息 请求时间:"+ sdf.format(new Date())+"**************************");
        log.info("请求request信息：URI = {}, path = {}，method = {} ", URIPath, path, method);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
