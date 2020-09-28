package com.llp.Bio;

import org.apache.log4j.net.SocketServer;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author llp
 * @date 2020/9/23 13:57
 */
public class BioTest {

    /*BIo 服务端*/
    @Test
    public void server() throws IOException {
        //如果有客户端，通过线程池创建通讯
        ExecutorService executorService = Executors.newCachedThreadPool();
        //创建SeverSocket服务端端口为6666
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了！");
        while (true) {
            System.out.println("链接到一个客户端");
            final Socket accept = serverSocket.accept();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler(accept);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public static void handler(Socket socket) throws IOException {
        try {
            System.out.println("当前线程的id为:"+Thread.currentThread().getId()+
                    "当前线程的名称为："+Thread.currentThread().getName());
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            while (true) {
                System.out.println("当前线程的id为:"+Thread.currentThread().getId()+
                        "当前线程的名称为："+Thread.currentThread().getName());
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
            } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("客户端链接关闭了");
            socket.close();
        }


    }

}
