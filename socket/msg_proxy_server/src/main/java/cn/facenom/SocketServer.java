package cn.facenom;

import cn.facenom.models.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端处理逻辑
 *
 * @author zyh
 * @version 1.0
 * @since 2023-03-06
 */
public class SocketServer {
    
    private SocketServer() {
        
    }

    public static void run(String[] args) {
        SocketServer socketServer = new SocketServer();
        socketServer.run();
    }

    private void run() {

        final int PORT = 9000;

        final Map<String, User> clients = new HashMap<>();

        ServerSocket server = null;

        try {
            server = new ServerSocket(PORT);
            String localAddress = server.getInetAddress().getHostAddress();
            int localPort = server.getLocalPort();
            System.out.println("server start, address: " + localAddress + ":" + localPort);

            while (true) {
                Socket client = server.accept();
                String clientAddress = client.getInetAddress().getHostAddress();
                int clientPort = client.getPort();
                String address = clientAddress + ":" + clientPort;
                System.out.println("client connected, address: " + address);

                User user = new User(clientAddress, clientPort, client);
                clients.put(address, user);

                SocketThread thread = new SocketThread(user, clients);
                thread.start();
            }

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
