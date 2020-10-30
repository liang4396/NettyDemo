package com.llp.nio;

import java.util.Scanner;

/**
 * @author llp
 * @date 2020/9/29 11:31
 */
public class ChatGroupClient2 {

    public static void main(String[] args) {
        //新建我们的客户端代码
        final ChatGroupClient chatGroupClient = new ChatGroupClient();
        new Thread(new Runnable() {
            public void run() {
                //每隔3秒读取从服务端发送的数据
                chatGroupClient.readMsg();
            }
        },"客户端1").start();
        System.out.println("请发送消息给其他人：");
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            String str = scan.next();
            chatGroupClient.sendMsg(str);
        }
    }
}
