package com.llp.netty.chatgroup.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.util.Scanner;

/**
 * @author llp
 * @date 2020/11/17 10:23
 */
public class ChatClient {

    private String host;
    private int prot;

    public ChatClient(String host, int prot) {
        this.host = host;
        this.prot = prot;
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("127.0.0.1", 8088);
        chatClient.run();
    }

    public void run() {
        EventLoopGroup client = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(client)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInit());
            System.out.println("客户端ok");
            //启动客户端连接服务器
            ChannelFuture sync = bootstrap.connect(host, prot).sync();

            Channel channel = sync.channel();
            System.out.println("-------------" + channel.localAddress() + "---------------");
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                //将消息发送到服务器端
                channel.writeAndFlush(msg);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            client.shutdownGracefully();
        }
    }
}
