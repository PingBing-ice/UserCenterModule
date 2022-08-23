package com.user.usercenter.ws;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Map;

/**
 * @author ice
 * @date 2022/7/14 11:01
 * 将httpSession设置到 配置对象里去
 */
//@Component
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
//    @Override
//    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//        HttpSession httpSession = (HttpSession) request.getHttpSession();
//        // 将httpseesion 存储到配置对象中
//        Map<String, Object> userProperties = sec.getUserProperties();
//        userProperties.put("HttpSession", httpSession);
//    }
}
