package com.user.netty.nettyHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author ice
 * @date 2022/7/22 21:13
 */
@Component
public class NettyListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private WebSocketNettyServer webSocketNettyServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            try {
                webSocketNettyServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
