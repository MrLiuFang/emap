package com.pepper.controller.emap.scoket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelMatchers;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @description:
 * @author: mr.liu
 * @create: 2020-09-27 10:49
 **/
@Component
public class TcpServer implements CommandLineRunner {

    @Value("${tcpPort:88}")
    private Integer port;

    @Autowired
    private ChannelGroupUtil channelGroupUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        runTcpServe();
    }

    private void runTcpServe() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); //bossGroup就是parentGroup，是负责处理TCP/IP连接的
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //workerGroup就是childGroup,是负责处理Channel(通道)的I/O事件

        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128) //初始化服务端可连接队列,指定了队列的大小128
                .childOption(ChannelOption.SO_KEEPALIVE, true) //保持长连接
                .childHandler(new ChannelInitializer<SocketChannel>() {  // 绑定客户端连接时候触发操作
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        ByteBuf delimiter = Unpooled.copiedBuffer("\n".getBytes());
                        sc.pipeline()
                                .addLast(new DelimiterBasedFrameDecoder(1024,delimiter))
                                .addLast(new StringDecoder(Charset.forName("UTF-8")))
                                .addLast(new StringEncoder(Charset.forName("UTF-8")))
                                .addLast(new ServerHandler());
                    }
                });
        //绑定监听端口，调用sync同步阻塞方法等待绑定操作完
        ChannelFuture future = sb.bind(port).sync();

        if (future.isSuccess()) {
            System.out.println("服务端启动成功");
        } else {
            System.out.println("服务端启动失败");
            future.cause().printStackTrace();
            bossGroup.shutdownGracefully(); //关闭线程组
            workerGroup.shutdownGracefully();
        }
        //成功绑定到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程。
        future.channel().closeFuture().sync();
    }

    class ServerHandler extends ChannelInboundHandlerAdapter{
        //接受client发送的消息
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String,Object> map  = objectMapper.readValue(String.valueOf(msg),Map.class);
                if (map.containsKey("type") && Integer.valueOf(map.get("type").toString())==1){
                    String userId = Objects.nonNull(map.get("userId"))?String.valueOf(map.get("userId")):"";
                    if (StringUtils.hasText(userId)){
                        redisTemplate.opsForValue().set(userId+"_tcp",ctx.channel().id());
                        List<String> list =  redisTemplate.opsForList().range(userId+"_tcp_cahce",0,-1);
                        if (Objects.nonNull(list)) {
                            list.forEach(message -> {
                                ctx.writeAndFlush(message + "\r\n");
                            });
                        }
                    }
                }else if (map.containsKey("type") && Integer.valueOf(map.get("type").toString())==2){
                    String userId = Objects.nonNull(map.get("userId"))?String.valueOf(map.get("userId")):"";
                    if (StringUtils.hasText(userId)){
                        Object obj = redisTemplate.opsForValue().get(userId+"_tcp");
                        if (Objects.nonNull(obj)){
                            ChannelId channelId = (ChannelId) obj;
                            Channel channel = channelGroupUtil.find(channelId);
                            if (Objects.nonNull(channel)){
                                Map<String,String>  map1 = new HashMap<String,String>();
                                map1.put("title","测试");
                                channel.writeAndFlush(objectMapper.writeValueAsString(map1) + "\r\n");
                            }
                        }
                    }
                }
            }catch (Exception ex){

            }
        }

        //通知处理器最后的channelRead()是当前批处理中的最后一条消息时调用
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("服务端接收数据完毕..");
            ctx.flush();
        }

        //读操作时捕获到异常时调用
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }

        //客户端去和服务端连接成功时触发
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            channelGroupUtil.add(ctx.channel());
            System.out.println("有客户端连接");
//            ctx.writeAndFlush("服务端测试下发消息\r\n");
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            channelGroupUtil.discard(ctx.channel());
            super.channelUnregistered(ctx);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            channelGroupUtil.discard(ctx.channel());
            super.handlerRemoved(ctx);
        }
    }
}


//{"type":1,"userId":"123456"}

//{"type":2,"userId":"123456"}