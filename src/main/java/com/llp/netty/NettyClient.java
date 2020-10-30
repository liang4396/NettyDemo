package com.llp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author llp
 * @date 2020/10/30 9:57
 */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException{

        //客户端只需要一个线程组
        EventLoopGroup group = new NioEventLoopGroup();
        //创建客户端创建对象
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)//设置客户端通道类型
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientHandler());//加入自己的处理器
                        }
                    });
            System.out.println("客户端ok");
            //启动客户端连接服务器
            ChannelFuture sync = bootstrap.connect("127.0.0.1", 6668).sync();
            //给关闭通道进行监听
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
