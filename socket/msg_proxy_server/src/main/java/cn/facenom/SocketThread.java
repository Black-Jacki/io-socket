package cn.facenom;

import cn.facenom.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static cn.facenom.Constants.*;

/**
 * 处理消息转发线程
 *
 * @author zyh
 * @version 1.0
 * @since 2023-03-05
 */
public class SocketThread extends Thread {

    private final User source;

    private final Map<String, User> clients;

    private final byte[] bytes = new byte[1024];

    InputStream inputStream;

    public SocketThread(User source, Map<String, User> clients) {
        this.source = source;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            inputStream = source.getSocket().getInputStream();
            int len;
            while ((len = inputStream.read(bytes)) > 0) {
                String type = new String(bytes, 0, 2, StandardCharsets.UTF_8);
                String address = bytes2AddressString();
                int contentLength = getContentLength();
                String content = new String(bytes, 10, contentLength, StandardCharsets.UTF_8);
                
                System.out.println("readLen: " + len 
                        + ", type: " + type 
                        + ", source: " + source.getAddress() + ":" + source.getPort() 
                        + ", target: " + address 
                        + ", contentLength: " + contentLength 
                        + ", content: " + content);

                switch (type) {
                    case TYPE_LOGIN:
                        login(content);
                        break;
                    case TYPE_LOGOUT:
                        logout();
                        break;
                    case TYPE_SEND:
                        send(address);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    private void login(String hostName) throws IOException {
        if (hostName != null && !hostName.isEmpty()) {
            source.setHostName(hostName);
            syncClients();
        }
    }

    private void logout() {
        String address = source.getAddress() + ":" + source.getPort();
        clients.remove(address);
        System.out.println(address + "已退出登录");
        syncClients();
        close();
    }

    private void syncClients() {
        System.out.println("clients: " + clients.toString());
        Set<String> keys = clients.keySet();
        for (String key : keys) {
            User user = clients.get(key);
            OutputStream outputStream = null;
            try {
                if (!user.getSocket().isClosed()) {
                    outputStream = user.getSocket().getOutputStream();
                    byte[] clientInfosBytes = clients.toString().getBytes();
                    byte[] infoBytes = new byte[1024];
                    infoBytes[0] = '0';
                    infoBytes[1] = '4';
                    byte[] ipPortBytes = address2Bytes("0.0.0.0", source.getSocket().getLocalPort());
                    System.arraycopy(ipPortBytes, 0, infoBytes, 2, 6);
                    System.arraycopy(len2bytes(clientInfosBytes.length), 0, infoBytes, 8, 2);
                    System.arraycopy(clientInfosBytes, 0, infoBytes, 10, clientInfosBytes.length);
                    
                    outputStream.write(infoBytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
                close(outputStream);
            }
        }
    }

    private void send(String address) throws IOException {
        User target = clients.get(address);
        if (target == null || target.getSocket().isClosed()) {
            OutputStream outputStream = source.getSocket().getOutputStream();
            String msg = target == null ? "主机不存在" : "主机连接中断";
            byte[] msgBytes = msg.getBytes();
            byte[] errorInfoBytes = new byte[1024];
            errorInfoBytes[0] = '0';
            errorInfoBytes[1] = '3';
            byte[] ipPortBytes = address2Bytes("0.0.0.0", source.getSocket().getLocalPort());
            System.arraycopy(ipPortBytes, 0, errorInfoBytes, 2, 6);
            System.arraycopy(len2bytes(msgBytes.length), 0, errorInfoBytes, 8, 2);
            System.arraycopy(msgBytes, 0, errorInfoBytes, 10, msgBytes.length);

            outputStream.write(errorInfoBytes);
            System.out.println(msg);
            return;
        }
         
        OutputStream outputStream = target.getSocket().getOutputStream();

        byte[] ipPortBytes = address2Bytes(target.getAddress(), target.getPort());
        byte[] sendBytes = bytes.clone();
        System.arraycopy(ipPortBytes, 0, sendBytes, 2, 6);
        outputStream.write(sendBytes);
        System.out.println("消息已发送");
    }

    private void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (source != null) {
                source.getSocket().close();
                String address = source.getAddress() + ":" + source.getPort();
                clients.remove(address);
                System.out.println(address + "已断开连接");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        syncClients();
    }

    private void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private String bytes2AddressString() throws IOException {
        byte[] ipBytes = new byte[4];
        byte[] portBytes = new byte[2];
        System.arraycopy(bytes, 2, ipBytes, 0, 4);
        System.arraycopy(bytes, 6, portBytes, 0, 2);

        InetAddress inetAddress = InetAddress.getByAddress(ipBytes);
        String address = inetAddress.getHostAddress();
        int port = ((portBytes[0] & 0xFF) << 8) | (portBytes[1] & 0xFF);
        return address + ":" + port;
    }
    
    private byte[] address2Bytes(String address, int port) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(address);
        byte[] ipBytes = inetAddress.getAddress();
        byte[] portBytes = new byte[2];
        portBytes[0] = (byte) (port >> 8 & 0xFF);
        portBytes[1] = (byte) (port & 0xFF);
        
        byte[] ipPortBytes = new byte[6];
        System.arraycopy(ipBytes, 0, ipPortBytes, 0, 4);
        System.arraycopy(portBytes, 0, ipPortBytes, 4, 2);
        return ipPortBytes;
    }
    
    private int getContentLength() {
        return  ((bytes[8] & 0xFF) << 8) | (bytes[9] & 0xFF);
    }
    
    private byte[] len2bytes(int len) {
        byte[] lenBytes = new byte[2];
        lenBytes[0] = (byte) (len >> 8 & 0xFF);
        lenBytes[1] = (byte) (len & 0xFF);
        return lenBytes;
    }
}
