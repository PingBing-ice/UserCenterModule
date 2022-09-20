package com.user.netty.nettyHandler;

import com.google.gson.Gson;
import com.user.model.constant.ChatType;
import com.user.model.domain.ChatRecord;
import com.user.model.domain.TeamChatRecord;
import com.user.model.domain.vo.ChatRecordVo;
import com.user.netty.utils.SpringUtilObject;
import com.user.openfeign.TeamOpenFeign;
import com.user.rabbitmq.config.mq.MqClient;
import com.user.rabbitmq.config.mq.RabbitService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author ice
 * @date 2022/7/22 16:27
 */
@Slf4j
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 用来保存所有的客服端连接
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 时间格式器 如果有需要可以将发送时间 发送的前端的好友
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        // 当接收到数据后自动调用
        String message = msg.text();

        Gson gson = new Gson();
        Message mess = gson.fromJson(message, Message.class);
        log.info(mess+"mess====================");
        RabbitService recordService = SpringUtilObject.getBean(RabbitService.class);
        switch (mess.getType()) {
            case ChatType.CONNECT:
                // 建立用户与通道的关联
                String userId = mess.getChatRecord().getUserId();
                UserChannelMap.put(userId, ctx.channel());
                log.info("建立用户 :" + userId + "与通道的关联: " + ctx.channel().id().asLongText());
                UserChannelMap.print();
                break;
            // 处理客服端发送消息
            case ChatType.FRIEND:
                // 将聊天消息保存到数据库
                ChatRecordVo chatRecord = mess.getChatRecord();
                // 发送消息好友在线,可以直接发送消息给好友
                Channel channel= UserChannelMap.getFriendChannel(chatRecord.getSendId());

                ChatRecord record = new ChatRecord();
                record.setUserId(chatRecord.getUserId());
                record.setFriendId(chatRecord.getSendId());
                record.setMessage(chatRecord.getMessage());

                if (channel != null) {
                    record.setHasRead(1);
                    recordService.sendMessage(MqClient.NETTY_EXCHANGE,MqClient.NETTY_KEY,record);
                    channel.writeAndFlush(new TextWebSocketFrame(gson.toJson(mess)));
                }else {
                    // 用户不在线 保存到数据库
                    record.setHasRead(0);
                    // 调用 Rabbit 保存信息
                    recordService.sendMessage(MqClient.NETTY_EXCHANGE,MqClient.NETTY_KEY,record);
                    // 不在线,暂时不发送
                    log.info("用户 "+chatRecord.getSendId() +"不在线!!!!");
                }
                break;
            case ChatType.TEAM:
                TeamOpenFeign team = SpringUtilObject.getBean(TeamOpenFeign.class);
                String teamId = mess.getChatRecord().getSendId();
                List<String> teamUserId = team.getUserTeamListById(teamId, mess.getChatRecord().getUserId());
                if (!CollectionUtils.isEmpty(teamUserId)) {
                    TeamChatRecord teamChatRecord = new TeamChatRecord();
                    teamChatRecord.setUserId(mess.getChatRecord().getUserId());
                    teamChatRecord.setTeamId(teamId);
                    teamChatRecord.setMessage(mess.getChatRecord().getMessage());
                    teamChatRecord.setHasRead(0);
                    teamUserId.forEach(id -> {
                        Channel teamUserChannel = UserChannelMap.getFriendChannel(id);
                        if (teamUserChannel != null) {
                            teamUserChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(mess)));
                        }
                    });
                    recordService.sendMessage(MqClient.NETTY_EXCHANGE,MqClient.TEAM_KEY,teamChatRecord);

                }else {
                    log.info("队伍人员为空: "+ message);
                }
                break;
            case ChatType.HEARTBEAT:
                log.info("接收心跳消息: "+ message);
                break;



        }

    }

    // 新的客服端连接时调用
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("创建连接{}",ctx.channel().id().asLongText());
        channels.add(ctx.channel());
    }

    // 出现异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("出现异常,关闭连接");
        cause.printStackTrace();
        // 通道 出现异常 移除该通道
        String channelId = ctx.channel().id().asLongText();
        UserChannelMap.removeByChannelId(channelId);
    }

    // channel 处于活动状态调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("服务器地址 上线了 ~ ====> "+ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    // 用户断开连接调用
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        // 通道断开连接时 移除该通道
        log.error("关闭连接 {}",ctx.channel().id().asLongText());
        String channelId = ctx.channel().id().asLongText();
        UserChannelMap.removeByChannelId(channelId);
    }
}
