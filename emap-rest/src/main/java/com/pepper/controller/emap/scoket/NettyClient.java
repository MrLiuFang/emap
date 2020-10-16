package com.pepper.controller.emap.scoket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * @description:
 * @author: mr.liu
 * @create: 2020-09-27 14:05
 **/
public class NettyClient {

    public static void main(String args[]) throws Exception {
        String host = "127.0.0.1";
        int port =88;
        Channel channel;
        final EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)  // 使用NioSocketChannel来作为连接用的channel类
                .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                    @Override
                    public void initChannel(SocketChannel socketChannel) throws Exception {
                        System.out.println("正在连接中...");
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        ByteBuf delimiter = Unpooled.copiedBuffer("\n".getBytes());
                        pipeline
                                .addLast(new DelimiterBasedFrameDecoder(1024,delimiter))
                                .addLast(new StringDecoder(Charset.forName("UTF-8")))
                                .addLast(new StringEncoder(Charset.forName("UTF-8")))
                                .addLast(new ClientHandler());

                    }
                });
        //发起异步连接请求，绑定连接端口和host信息
        final ChannelFuture future = b.connect(host, port).sync();

        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture arg0) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("连接服务器成功");

                } else {
                    System.out.println("连接服务器失败");
                    future.cause().printStackTrace();
                    group.shutdownGracefully(); //关闭线程组
                }
            }
        });
        channel = future.channel();
        channel.writeAndFlush("测试\r\n");
    }

    static class ClientHandler extends SimpleChannelInboundHandler<String>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("接受到server响应数据: " + msg);
        }
    }
}
