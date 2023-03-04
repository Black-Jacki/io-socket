package cn.facenom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 一个多线程消息转发代理程序
 * @author zyh
 * @version 1.0
 * @since 2023-03-05
 */
public class Main {
    public static void main(String[] args) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(9000);
            System.out.println("server start, address: " + server.getLocalSocketAddress());

            Socket client1 = server.accept();
            System.out.println("client1 connected, address: " + client1.getRemoteSocketAddress());

            Socket client2 = server.accept();
            System.out.println("client2 connected, address: " + client2.getRemoteSocketAddress());

            SocketThread thread1 = new SocketThread(client1, client2);
            thread1.start();

            SocketThread thread2 = new SocketThread(client2, client1);
            thread2.start();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}