package com.delaunay.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 服务端
 */
public class NettyServer {
    private static final int PORT = 1000;

    public static void main(String[] args) {
        // bossGroup表示监听端口，accept新连接的线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // workerGroup表示处理每一条连接的数据读写的线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        // 引导类ServerBootstrap将引导我们进行服务端的启动工作
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup) // 配置上述两个线程组，定型线程模型
                .channel(NioServerSocketChannel.class) // 指定我们服务端的 IO 模型为NIO
                .childHandler(new ChannelInitializer<NioSocketChannel>() { // 定义后续每条连接的数据读写，业务处理逻辑
                    protected void initChannel(NioSocketChannel ch) {
                        // 获取服务端侧关于这条连接的逻辑处理链 pipeline，然后添加一个逻辑处理器，负责读取客户端发来的数据
                        ch.pipeline().addLast(new FirstServerHandler());
                    }
                });

        // serverBootstrap.bind(8000);
        bind(serverBootstrap, PORT);
    }
    /**
     * 总结
     * 要启动一个Netty服务端，必须要指定三类属性，分别是线程模型、IO 模型、连接读写处理逻辑
     * 有了这三者，之后在调用bind(8000)，我们就可以在本地绑定一个 8000 端口启动起来
     */


    /**
     * 从port号端口往上找一个端口，直到这个端口能够绑定成功
     * @param serverBootstrap
     * @param port
     */
    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> future) {
                if (future.isSuccess()) {
                    System.out.println("端口[" + port + "]绑定成功!");
                } else {
                    System.err.println("端口[" + port + "]绑定失败!");
                    bind(serverBootstrap, port + 1);
                }
            }
        });
    }
}
