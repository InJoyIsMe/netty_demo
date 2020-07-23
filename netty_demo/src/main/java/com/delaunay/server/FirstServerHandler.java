package com.delaunay.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.Date;

public class FirstServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        // 接收客户端发送的数据并打印
        // 这里的 msg 参数指的就是 Netty 里面数据读写的载体
        // TODO 为什么需要强转ByteBuf类型
        ByteBuf byteBuf = (ByteBuf)msg;

        System.out.println(new Date() + "：服务端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));

        // 回复数据到客户端
        System.out.println(new Date() + "：服务端写出数据");
        ByteBuf out = getByteBuf(ctx);
        // 调用 writeAndFlush() 方法写出去
        ctx.channel().writeAndFlush(out);
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        byte[] bytes = "你好，欢迎关注我的博客 delaunay.cn".getBytes(Charset.forName("utf-8"));
        // 先创建一个 ByteBuf，然后填充二进制数据
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(bytes);

        return buffer;
    }
}
