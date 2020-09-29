package com.llp.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author llp
 * @date 2020/9/29 10:45
 */

public class ChatGroupClient {

    //定义属性
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;



    //构造器完成初始化工作
    public ChatGroupClient() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(userName + "is ok.....");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给其他客户端
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        msg = userName + "说：" + msg;
        try {
            ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes());
            socketChannel.write(wrap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 客户端接收其他客户端发送的消息
     */
    public void readMsg() {
        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    if (selectionKey.isReadable()) {
                        SocketChannel schannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        schannel.read(buffer);
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //新建我们的客户端代码
        ChatGroupClient chatGroupClient = new ChatGroupClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //每隔3秒读取从服务端发送的数据
                chatGroupClient.readMsg();
            }
        }, "客户端1").start();
        System.out.println("请发送消息给其他人：");
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            String str = scan.next();
            chatGroupClient.sendMsg(str);
        }
    }


}
