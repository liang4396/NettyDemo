package com.llp.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author llp
 * @date 2020/10/30 10:11
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /*通道就绪就会触发这个方法*/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive client ctx:" + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 服务端 (#^.^#)", CharsetUtil.UTF_8));
    }

    /*有消息可读时*/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead client ctx:" + ctx + ".ctx 地址为" + ctx.channel().remoteAddress());
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("服务器回复的消息是:" + byteBuf.toString(CharsetUtil.UTF_8));
    }

    /*
出现异常关闭通道
 */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
