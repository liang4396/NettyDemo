package com.llp.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author llp
 * @date 2020/9/21 13:33
 * 非阻塞式SocketChannel
 */
public class NoBlockingSocketChannel {

    /*客户端
     * 非阻塞模式
     * */
    @Test
    public void client() throws IOException {
        /*生成发送端通道*/
        SocketChannel schannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        //设置为非阻塞模式
        schannel.configureBlocking(false);
        //指定缓冲区大小
        ByteBuffer buf = ByteBuffer.allocate(1024);
        //发送数据到服务端
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            String str = scan.next();
            buf.put((new Date().toString() + "\n" + str).getBytes());
            buf.flip();
            schannel.write(buf);
            buf.clear();
        }

        //关闭通道
        schannel.close();
    }

    /*
     * 服务端
     * 非阻塞模式*/
    @Test
    public void server() throws IOException {
        //获取服务端通道
        ServerSocketChannel sschannel = ServerSocketChannel.open();
        //设置非阻塞模式
        sschannel.configureBlocking(false);
        //绑定端口
        sschannel.bind(new InetSocketAddress(9898));
        //获取选择器
        Selector selector = Selector.open();
        //将通道注册在选择器上，并监听接收事件
        sschannel.register(selector, SelectionKey.OP_ACCEPT);
        // 6. 轮询式的获取选择器上已经“准备就绪”的事件
        while (selector.select() > 0) {
            // 7. 获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                // 8. 获取准备“就绪”的事件
                SelectionKey next = iterator.next();
                // 9. 判断具体是什么事件准备就绪
                if (next.isAcceptable()) {
                    System.out.println("-------next.isAcceptable()");
                    // 10. 若“接收就绪”，获取客户端连接
                    SocketChannel schannel = sschannel.accept();
                    // 11. 切换非阻塞模式
                    schannel.configureBlocking(false);
                    //将该通道注册到选择器上
                    schannel.register(selector, SelectionKey.OP_READ);
                } else if (next.isReadable()) {
                    // 13. 获取当前选择器上“读就绪”状态的通道
                    SocketChannel schannel = (SocketChannel) next.channel();
                    System.out.println("获取到客户端的通道");
                    //指定缓冲区大小 读取通道中的数据
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = schannel.read(buf)) > 0) {
                        buf.flip();
                        System.out.println(new String(buf.array(), 0, len));
                        buf.clear();
                    }

                }
                // 15. 取消选择键 SelectionKey
                iterator.remove();
            }
        }
    }
}
