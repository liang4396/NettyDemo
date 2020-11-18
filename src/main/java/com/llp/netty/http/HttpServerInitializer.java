package com.llp.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author llp
 * @date 2020/11/12 9:20
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();
        /**
         * 加入一个Netty 提供为HttpServerCodec
         * 是netty提供处理Http的编码和解码器
         */
        System.out.println("加入处理器.....");
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
        pipeline.addLast("MyHttpServerhandler",new HttpServerhandler());

    }
}
