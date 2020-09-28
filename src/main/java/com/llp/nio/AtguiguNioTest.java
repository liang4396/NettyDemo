package com.llp.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author llp
 * @date 2020/9/28 13:22
 */
public class AtguiguNioTest {

    @Test
    public void client() throws IOException {
        //获取一个SocketChannel
        SocketChannel schannel = SocketChannel.open();
        //设置为不阻塞
        schannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 9898);
        //链接服务器端口
        if (!schannel.connect(inetSocketAddress)) {
            while (!schannel.finishConnect()) {
                System.out.println("因为链接需要时间,客户端不会阻塞，可以进行其他工作");
            }
        }
        System.out.println("我的hashcode是"+schannel.hashCode());
        String str = "hello ，梁雷鹏你好！";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        //发送数据到服务端
        buf.put(str.getBytes());
        buf.flip();
        schannel.write(buf);
        buf.clear();

        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void server() throws IOException {
        //服务端通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        //选择器
        Selector selector = Selector.open();
        //设置不阻塞
        ssChannel.configureBlocking(false);
        //绑定短剑9898
        ssChannel.bind(new InetSocketAddress(9898));
        //把服务端通道注册到选择器，并且关心事件为accept（接受(建议、邀请等);）
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select(1000) == 0) {
                System.out.println("等待了1秒，没有人链接我！");
                continue;
            }
            //获取关注事件集合（获取关注的通道事件）通过迭代器关注
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                //获取对应的SelectionKey
                SelectionKey next = iterator.next();
                //SelectionKey判断是否有相应的事件发生
                if (next.isAcceptable()) {//OP ACCEPT，有新的客户端链接
                    SocketChannel accept = ssChannel.accept();
                    // 切换非阻塞模式
                    accept.configureBlocking(false);
                    //将注册到服务端的新通道也注册到Selector
                    accept.register(selector, SelectionKey.OP_READ);
                    System.out.println("有客户端连接到我了！"+accept.hashCode());
                } else if (next.isReadable()) {//发生Read事件
                    //强转为SocketChannel
                    SocketChannel channel = (SocketChannel) next.channel();
                    //获取到通道相关联的Buffer
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    int read = channel.read(buf);
                    if (read > 0) {
                        buf.flip();
                        System.out.println(new String(buf.array(), 0, read));
                        buf.clear();
                    }
                }
                iterator.remove();
            }
        }
    }

}
