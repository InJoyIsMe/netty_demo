package com.delaunay.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 客户端
 */
public class NettyClient {
    private static final int MAX_RETRY = 5;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1000;

    public static void main(String[] args) {
        // 建立处理连接的线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        // 引导客户端的启动
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
            // 1.指定线程模型
            .group(workerGroup)
            // 2.指定IO类型为NIO
            .channel(NioSocketChannel.class)
            // 3.IO处理逻辑
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    // 指定连接数据读写逻辑
                    // ch.pipeline() 返回的是和这条连接相关的逻辑处理链，采用了责任链模式
                    // 调用 addLast() 方法 添加一个逻辑处理器，这个逻辑处理器为的就是在客户端建立连接成功之后，向服务端写数据
                    ch.pipeline().addLast(new FirstClientHandler());
                }
            });

        // 4.建立连接
        connect(bootstrap, HOST, PORT, MAX_RETRY);
        /*
        bootstrap.connect("localhost", 80).addListener(future -> {
            if (future.isSuccess()){
                System.out.println("连接成功！");
            } else {
                System.out.println("连接失败！");
                // 再次连接
            }
        });
        */
    }

    /**
     * 失败重连
     * 通常情况下，连接建立失败不会立即重新连接，而是会通过一个指数退避的方式，
     * 比如每隔 1 秒、2 秒、4 秒、8 秒，以 2 的幂次来建立连接，然后到达一定次数之后就放弃连接，
     * 接下来我们就来实现一下这段逻辑，我们默认重试 5 次
     * @param bootstrap
     * @param host
     * @param port
     * @param retry
     */
    private static void connect(Bootstrap bootstrap, String host, int port, int retry){
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()){
                System.out.println("连接成功！");
            } else if (retry==0) {
                System.out.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(
                        () -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS
                );

            }
        });
    }


}













