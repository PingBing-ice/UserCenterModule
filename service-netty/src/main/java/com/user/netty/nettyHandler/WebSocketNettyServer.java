package com.user.netty.nettyHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * @author ice
 * @date 2022/7/22 15:55
 */
@Component
@Log4j2
public class WebSocketNettyServer {

    // 创建两个线程池
    private NioEventLoopGroup mainGroup;  // 主
    private NioEventLoopGroup subGroup;  // 从

    // 创建Netty服务启动对象
    private final ServerBootstrap serverBootstrap;
    private ChannelFuture future;

    public void start() {
        future = serverBootstrap.bind(9001);
        log.info("netty-server 启动成功");
    }

    public WebSocketNettyServer() {
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        // 初始化服务器启动对象
        serverBootstrap.
                group(mainGroup, subGroup)
                // 指定Netty 通道类型
                .channel(NioServerSocketChannel.class)
                .childHandler(new WebSocketChannelInitializer());
    }
    //    public static void main(String[] args) {
//
//        // 创建两个线程池
//        NioEventLoopGroup mainGroup = new NioEventLoopGroup(); // 主
//        NioEventLoopGroup subGroup = new NioEventLoopGroup(); // 从
//        try {
//            // 创建Netty服务启动对象
//            ServerBootstrap bootstrap = new ServerBootstrap();
//
//            // 初始化服务器启动对象
//            bootstrap.
//                    group(mainGroup, subGroup).
//                    // 指定Netty 通道类型
//                            channel(NioServerSocketChannel.class)
//                    .childHandler(new WebSocketChannelInitializer());
//            // 绑定服务器端口,以同步的方式启动服务器
//            ChannelFuture future = bootstrap.bind(9090).sync();
//            // 等待服务器的关闭
//            future.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            mainGroup.shutdownGracefully();
//            subGroup.shutdownGracefully();
//        }
//
//
//    }

}
