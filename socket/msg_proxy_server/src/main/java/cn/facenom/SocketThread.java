package cn.facenom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 处理消息转发线程
 * @author zyh
 * @version 1.0
 * @since 2023-03-05
 */
public class SocketThread extends Thread {

    private final Socket client1;
    private final Socket client2;

    private final byte[] bytes = new byte[1024];

    public SocketThread(Socket client1, Socket client2) {
        this.client1 = client1;
        this.client2 = client2;
    }

    @Override
    public void run() {
        try {
            InputStream its = client1.getInputStream();
            OutputStream ots = client2.getOutputStream();

            int len;
            while ((len = its.read(bytes)) > 0) {
                System.out.println("接收到client1: " + client1.getPort() 
                        + " 的数据，正在转发给client2: " + client2.getPort() 
                        + " data: " + new String(bytes, 0, len, StandardCharsets.UTF_8));
                ots.write(bytes, 0, len);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (client1 != null) {
                    client1.close();
                }
                if (client2 != null) {
                    client2.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
