package com.llp.netty.chatgroup.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author llp
 * @date 2020/11/17 9:34
 */
public class ChatServerInit extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //获取HandlerContext 双向管道集合
        ChannelPipeline pipeline = socketChannel.pipeline();
        //新增一个解码器
        pipeline.addLast("decoder", new StringDecoder());
        //新增一个编码器
        pipeline.addLast("encoder", new StringEncoder());
        //自己的业务处理
        pipeline.addLast(new ChatServerHandler());

        /**
         * IdleStateHandler提供时间空闲处理器
         * read..Time 表示多长时间没有读
         * wirte..Time 表示多长时间没有写
         * alldleTime 表示多长时间没有读写 就会发送一个心跳检测是否连接
         */
        pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));
    }
}
