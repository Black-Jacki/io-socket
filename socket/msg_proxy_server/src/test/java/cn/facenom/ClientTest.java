package cn.facenom;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author zyh
 * @version 1.0
 * @since 2023/11/4
 */

public class ClientTest {

    @Test
    public void connectUser1() {
        try {
            // 随机本地 socket client 端口用法
            Socket socket = new Socket("127.0.0.1", 9000);
            
            // 指定本地 socket client 端口用法
//            Socket socket = new Socket();
//            socket.bind(new InetSocketAddress("127.0.0.1", 55685));
//            socket.connect(new InetSocketAddress("127.0.0.1", 9000));

            String localAddress = socket.getLocalAddress().getHostAddress();
            int localPort = socket.getLocalPort();

            String remoteAddress = socket.getInetAddress().getHostAddress();
            int remotePort = socket.getPort();

            System.out.println("local: " + localAddress + ":" + localPort + " -> "
                    + "remote: " + remoteAddress + ":" + remotePort);

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            if (outputStream != null) {
                byte[] ipBytes = socket.getLocalAddress().getAddress();
                byte[] portBytes = new byte[2];
                portBytes[0] = (byte) (localPort >> 8 & 0xFF);
                portBytes[1] = (byte) (localPort & 0xFF);
                byte[] hostNameBytes = "剑侠客".getBytes(StandardCharsets.UTF_8);

                // type[2] + ip[4] + port[2] + contentLength[2] + 客户端昵称所占字节长度
                byte[] bytes = new byte[1024];
                bytes[0] = '0';
                bytes[1] = '0';
                System.arraycopy(ipBytes, 0, bytes, 2, 4);
                System.arraycopy(portBytes, 0, bytes, 6, 2);
                bytes[8] = (byte) (hostNameBytes.length >> 8 & 0xFF);
                bytes[9] = (byte) (hostNameBytes.length & 0xFF);
                System.arraycopy(hostNameBytes, 0, bytes, 10, hostNameBytes.length);

                outputStream.write(bytes);
            }

            if (inputStream != null) {
                byte[] bytes = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) > 0) {
                    String type = new String(bytes, 0, 2, StandardCharsets.UTF_8);
                    byte[] ipBytes = new byte[4];
                    System.arraycopy(bytes, 2, ipBytes, 0, 4);
                    InetAddress inetAddress = InetAddress.getByAddress(ipBytes);
                    String address = inetAddress.getHostAddress();
                    int port = ((bytes[5] & 0xFF) << 8) | (bytes[6] & 0xFF);
                    int contentLength = ((bytes[8] & 0xFF) << 8) | (bytes[9] & 0xFF);
                    String content = new String(bytes, 10, contentLength, StandardCharsets.UTF_8);

                    System.out.println("readLen: " + len
                            + ", type: " + type
                            + ", target: " + address + ":" + port
                            + ", contentLength: " + contentLength
                            + ", content: " + content);
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void connectUser2() {
        int targetPort = 55685;
        try {
            Socket socket = new Socket("127.0.0.1", 9000);

            String localAddress = socket.getLocalAddress().getHostAddress();
            int localPort = socket.getLocalPort();

            String remoteAddress = socket.getInetAddress().getHostAddress();
            int remotePort = socket.getPort();

            System.out.println("local: " + localAddress + ":" + localPort + " -> "
                    + "remote: " + remoteAddress + ":" + remotePort);

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            if (outputStream != null) {
                byte[] ipBytes = socket.getLocalAddress().getAddress();
                byte[] portBytes = new byte[2];
                portBytes[0] = (byte) (localPort >> 8 & 0xFF);
                portBytes[1] = (byte) (localPort & 0xFF);
                byte[] hostNameBytes = "逍遥生".getBytes(StandardCharsets.UTF_8);

                // type[2] + ip[4] + port[2] + contentLength[2] + 客户端昵称所占字节长度
                byte[] bytes = new byte[1024];
                bytes[0] = '0';
                bytes[1] = '0';
                System.arraycopy(ipBytes, 0, bytes, 2, 4);
                System.arraycopy(portBytes, 0, bytes, 6, 2);
                bytes[8] = (byte) (hostNameBytes.length >> 8 & 0xFF);
                bytes[9] = (byte) (hostNameBytes.length & 0xFF);
                System.arraycopy(hostNameBytes, 0, bytes, 10, hostNameBytes.length);

                outputStream.write(bytes);
            }

            sendMessage(outputStream, "127.0.0.1", targetPort);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(OutputStream outputStream, String address, int port) {
        System.out.println("即将发送消息");
        try {
            if (outputStream != null) {
                InetAddress inetAddress = InetAddress.getByName(address);
                byte[] ipBytes = inetAddress.getAddress();
                byte[] portBytes = new byte[2];
                portBytes[0] = (byte) (port >> 8 & 0xFF);
                portBytes[1] = (byte) (port & 0xFF);
                byte[] contentBytes = "你好剑侠客".getBytes();

                // type[2] + ip[4] + port[2] + contentLength[2] + 内容所占字节长度
                byte[] bytes = new byte[1024];
                bytes[0] = '0';
                bytes[1] = '2';
                System.arraycopy(ipBytes, 0, bytes, 2, 4);
                System.arraycopy(portBytes, 0, bytes, 6, 2);
                bytes[8] = (byte) (contentBytes.length >> 8 & 0xFF);
                bytes[9] = (byte) (contentBytes.length & 0xFF);
                System.arraycopy(contentBytes, 0, bytes, 10, contentBytes.length);
                
                outputStream.write(bytes);
                System.out.println("已发送消息");
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                outputStream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Test
    public void ipTest() {
        String ip = "0.0.0.0";
        int port = 9000;
        try {
            InetAddress address = InetAddress.getByName(ip);
            byte[] ipBytes = address.getAddress();
            byte[] portBytes = new byte[2];
            portBytes[0] = (byte) (port >> 8 & 0xFF);
            portBytes[1] = (byte) (port & 0xFF);
            byte[] ipPortBytes = new byte[6];
            System.arraycopy(ipBytes, 0, ipPortBytes, 0, 4);
            System.arraycopy(portBytes, 0, ipPortBytes, 4, 2);
            System.out.println(Arrays.toString(ipPortBytes));

            byte[] ipBytes2 = new byte[4];
            byte[] portBytes2 = new byte[2];
            System.arraycopy(ipPortBytes, 0, ipBytes2, 0, 4);
            System.arraycopy(ipPortBytes, 4, portBytes2, 0, 2);

            String ip2 = InetAddress.getByAddress(ipBytes2).getHostAddress();
            int port2 = ((portBytes2[0] & 0xFF) << 8) | (portBytes2[1] & 0xFF);
            System.out.println("ip and port: " + ip2 + ":" + port2);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
