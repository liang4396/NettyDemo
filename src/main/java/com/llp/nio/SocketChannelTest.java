package com.llp.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author llp
 * @date 2020/9/21 10:25
 * 阻塞式SocketChannel 演示
 */
public class SocketChannelTest {

    /**
     * 客户端
     */
    @Test
    public void client() throws IOException {
        //10.114.3.100" 获取通道
        SocketChannel schannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        FileChannel inChannle = FileChannel.open(Paths.get("F:/FileChannel/1.png"), StandardOpenOption.READ);

        //分配指定缓冲区大小
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //将本地文件通过buffer写入socketChannel 发送到服务端
        while (inChannle.read(byteBuffer) != -1) {
            byteBuffer.flip();
            schannel.write(byteBuffer);
            byteBuffer.clear();
        }
        schannel.shutdownOutput();//因为是阻塞模式，需要告诉服务端发送完毕

        int len = 0;
        while ((len = schannel.read(byteBuffer)) != -1) {
            byteBuffer.flip();
            System.out.println("len :" +len);
            System.out.println(new String(byteBuffer.array(), 0, len));
            byteBuffer.clear();
        }
        //关闭通道
        inChannle.close();
        schannel.close();
    }

    /**
     * 服务端
     */
    @Test
    public void server() throws IOException {
        //获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        //本地文件操作通道
        FileChannel outChannel = FileChannel.open(Paths.get("F:/FileChannel/2.png"), StandardOpenOption.WRITE,
                StandardOpenOption.READ);
        //绑定链接
        ssChannel.bind(new InetSocketAddress(9898));
        //获取客户端链接
        SocketChannel sChannel = ssChannel.accept();
        //分配指定缓冲区大小
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (sChannel.read(buffer) != -1) {
            buffer.flip();
            outChannel.write(buffer);
            buffer.clear();
        }
        // 发送反馈给客户端
        buffer.put("服务端接收数据成功".getBytes());
        buffer.flip();
        sChannel.write(buffer);

        // 6. 关闭通道
        sChannel.close();
        outChannel.close();
        ssChannel.close();


    }
}
