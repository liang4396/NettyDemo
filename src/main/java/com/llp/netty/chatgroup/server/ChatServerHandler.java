package com.llp.netty.chatgroup.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author llp
 * @date 2020/11/17 9:53
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    //定义一个Channels数组，管理所有的channel
    //GlobalEventExecutor.INSTANCE是一个全局执行事件是一个单列
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /*客户端加入时所做的第一个操作*/
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //将新加入的客户端 放入 Channls中进行管理
        Channel channel = ctx.channel();
        /*该方法会给所有channls中的channle发送消息 不需要我们自己遍历*/
        channels.writeAndFlush("[客户端]" + channel.remoteAddress() + "上线了！" + sdf.format(new Date()) + "\n");
        channels.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //将新加入的客户端 放入 Channls中进行管理
        Channel channel = ctx.channel();
        /*该方法会给所有channls中的channle发送消息 不需要我们自己遍历*/
        channels.writeAndFlush("[客户端]" + channel.remoteAddress() + "离线了！"+ sdf.format(new Date()) + "\n");
    }

    /**
     * 表示channle 处于活动状态
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "在服务器端 上线了！");
    }

    /**
     * 服务器 发现channle处于非活动状态
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "在服务器端 下线了！");
    }

    /**
     * 读取数据 并且转发
     *
     * @param ctx
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        //获取到当前自己的Channel
        Channel myChanel = ctx.channel();
        channels.forEach(ch -> {
            if (myChanel != ch) {
                ch.writeAndFlush("[客户端]" + myChanel.remoteAddress() + "说:" + s);
            } else {
                ch.writeAndFlush("[自己]说:" + s);
            }
        });

    }

    /*
    出现异常关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
