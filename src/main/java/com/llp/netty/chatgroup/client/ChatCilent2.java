package com.llp.netty.chatgroup.client;

/**
 * @author llp
 * @date 2020/11/17 11:07
 */
public class ChatCilent2 {
    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("127.0.0.1", 8088);
        chatClient.run();
    }
}
