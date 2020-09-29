package com.llp.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author llp
 * @date 2020/9/29 9:30
 * 群聊服务端
 * 1 服务器启动并监听
 * 2 服务器接收客户端消息并实现转发（处理上线离线）
 */
public class ChatGroupServer {
    //定义属性 选择器
    private Selector selector;
    //监听通道（服务器通道）
    private ServerSocketChannel listenSocket;


    public ChatGroupServer() {
        try {
            //创建选择器
            selector = Selector.open();
            //创建服务器监听通道
            listenSocket = ServerSocketChannel.open();
            //绑定服务端端口9898
            listenSocket.bind(new InetSocketAddress(9898));
            //设置通道为非阻塞
            listenSocket.configureBlocking(false);
            //注册通道到选择器上 监听通道上的 接收（注册）事件
            listenSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    private void listen() {
        try {
            while (true) {
                //阻塞2秒。发现是否有注册在通道上的事件发生
                int select = selector.select(2000);
                if (select > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        //判断是否有接受事件（注册）
                        if (selectionKey.isAcceptable()) {
                            //获取接收事件的通道（注册通道）
                            SocketChannel schannel = listenSocket.accept();
                            //设置通道为非阻塞
                            schannel.configureBlocking(false);
                            System.out.println(schannel.getRemoteAddress() + "：上线了！");
                            schannel.register(selector, SelectionKey.OP_READ);
                        }
                        // 判断是否可有读取事件
                        if (selectionKey.isReadable()) {//通道有可以读取的事件
                            //处理客户端发送的消息（读取操作）
                            readData(selectionKey);
                        }
                        iterator.remove();
                    }
                } else {
                    /* System.out.println("等待中。。。没有客户端连接");*/
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 读取通道发来的数据
     *
     * @param selectionKey
     */
    private void readData(SelectionKey selectionKey) {
        SocketChannel socketChannel = null;
        try {
            //获取客户端连接的通道
            socketChannel = (SocketChannel) selectionKey.channel();
            //生成缓冲区并设置大小
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //将数据读取到bffer中
            int read = socketChannel.read(buffer);
            if (read > 0) {
                //将数据转换为字符串
                String data = new String(buffer.array());
                //将字符串发送给其他客户端
                sendMsgToOther(data, socketChannel);
            }
        } catch (IOException e) {
            try {
                System.out.println(socketChannel.getRemoteAddress() + "离线了！");
                //取消注册
                selectionKey.cancel();
                //关闭通道
                socketChannel.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 发送消息到除自己的所有客户端中
     *
     * @param msg
     * @param selfChannel
     */
    private void sendMsgToOther(String msg, SocketChannel selfChannel) {
        try {
            System.out.println("服务器转发消息中！");
            //获取selector上所有注册的通道
            Set<SelectionKey> keys = selector.keys();
            for (SelectionKey key : keys) {

                SelectableChannel targetChannel = key.channel();
                //遍历所有通道如果不是自己,则发送消息
                if (targetChannel instanceof SocketChannel && targetChannel != selfChannel) {
                    //强转为SocketChannel
                    SocketChannel target = (SocketChannel) targetChannel;
                    //将消息存储到buffer中
                    ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                    System.out.println("服务器转发消息给" + target.getRemoteAddress() + "成功！");
                    target.write(buffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Test
    public void server() {

    }

    public static void main(String[] args) {
        //启动我们的客户端
        ChatGroupServer chatGroupServer = new ChatGroupServer();
        chatGroupServer.listen();
    }
}
