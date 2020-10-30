package com.llp.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;


/**
 * @author llp
 * @date 2020/10/29 10:58
 * 自定义handler 需要继承对netty规定好的某个handleradapter规范
 * 这时自顶一个handler才能称为一个handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /*
     * 读取客户端实际发送的消息
     * ChannelHandlerContext ctx 上下文对象 含有管道 pipeline 通道 channel 地址
     * msg 就是客户端发送过来的消息 默认是Object类型*/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx :" + ctx);
        //将msg转成bytebuf 是netty提供的
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是 msg :" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址是:" + ctx.channel().remoteAddress());
    }

    /*
     * 数据读取完毕*/
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //write Flush 是write+Flush
        //将数据写入缓存并刷新
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端", CharsetUtil.UTF_8));
    }

    /*
    出现异常关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
