package com.user.usercenter.ws;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.user.util.common.ErrorCode;
//import com.user.usercenter.entity.User;
//import com.user.usercenter.entity.socket_.Message;
//import com.user.usercenter.entity.socket_.MessageUtils;
//import com.user.util.exception.GlobalException;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpSession;
//import javax.websocket.*;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static com.user.usercenter.constant.UserConstant.USER_LOGIN_STATE;
//
///**
// * @author ice
// * @date 2022/7/14 10:27
// */
//@ServerEndpoint(value = "/chat",configurator = GetHttpSessionConfigurator.class)
//@Component
//@Log4j2
public class ChatEndpoint {
//
//
//    // 用来存储每一个客户端对应的 ChatEndpoint 对象
//    private static final Map<String, ChatEndpoint> onlineUsers = new ConcurrentHashMap<>();
//
//    private final Gson gson = new Gson();
//    // 声明session 对象 ,通过该对象可以发送消息给指定的客户端
//    private Session session;
//
//    // 声明一个 httpSession对象 储存了之前在httpSession 存储的对象
//    private HttpSession httpSessions;
//
//    /**
//     * 连接建立时被调用
//     *
//     * @param session
//     * @param config
//     */
//    @OnOpen
//    public void onOpen(Session session, EndpointConfig config) {
//        // 将局部 session 赋值给成员session 拿到连接时的session
//        this.session = session;
//        // 通过配置对象 获取httpSession 的对象 设置为全局
//        this.httpSessions = (HttpSession) config.getUserProperties().get("HttpSession");
//        // 获取登录的用户
//        User user = (User) httpSessions.getAttribute(USER_LOGIN_STATE);
//        // 将登录的用户作为key 当前的对象作为 value 保存的map里
//        onlineUsers.put(user.getUsername(), this);
//        // 将当前在线用户的用户名推送给所有的用户名
//        // 1. 获取消息
//        String message = MessageUtils.getMessage(true, null, getNames());
//        // 2.调用方法进行系统消息的推送 给所有的人
//        broadcastAllUsers(message);
//    }
//
//    /**
//     * 系统消息的推送
//     * @param message
//     */
//    private void broadcastAllUsers(String message) {
//        try {
//            Set<String> names = onlineUsers.keySet();
//            for (String name : names) {
//                ChatEndpoint chatEndpoint = onlineUsers.get(name);
//                // 进行消息推送
//                chatEndpoint.session.getBasicRemote().sendText(message);
//            }
//        } catch (Exception e) {
//            log.info(e+"===================================================");
//            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "消息发送失败"+e);
//        }
//    }
//
//    private Set<String> getNames() {
//        return onlineUsers.keySet();
//    }
//
//    /**
//     * 接收到客服端发送的数据是被调用
//     * 消息中间站
//     * @param message 客户端发过来的信息
//     * @param session
//     */
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        // 将message消息字符串转换成message对象
//        Message mess = gson.fromJson(message, new TypeToken<Message>() {
//        }.getType());
////        mapper.readValue(message, Message.class);
//        String toName = mess.getToName();
//        String toMessage = mess.getMessage();
//        // 获取当前登录的用户
//        User user = (User) httpSessions.getAttribute(USER_LOGIN_STATE);
//        // 获取推送给指定用户的消息格式数据
//        String resultMessage = MessageUtils.getMessage(false, user.getUsername(), toMessage);
//        // 发送数据
//        try {
//            // 获取要发送的用户的session 进行发送
//            onlineUsers.get(toName).session.getBasicRemote().sendText(resultMessage);
//        } catch (IOException e) {
//            log.info(e);
//            throw new GlobalException(ErrorCode.SYSTEM_EXCEPTION, "发送消息失败");
//        }
//
//    }
//
//    /**
//     * 连接关闭
//     *
//     * @param session
//     */
//    @OnClose
//    public void onClose(Session session) {
//        User user = (User) httpSessions.getAttribute(USER_LOGIN_STATE);
//        String username = user.getUsername();
//        // 从容器中删除指定的用户
//        onlineUsers.remove(username);
//        // 获取推送的消息
//        String message = MessageUtils.getMessage(true, null, getNames());
//        broadcastAllUsers(message);
//    }
}
