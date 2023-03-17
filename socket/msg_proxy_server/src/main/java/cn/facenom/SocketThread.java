package cn.facenom;

import cn.facenom.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * 处理消息转发线程
 * @author zyh
 * @version 1.0
 * @since 2023-03-05
 */
public class SocketThread extends Thread {

    private final User source;
    
    private User target;
    
    private final Map<String, User> clients;

    private final byte[] bytes = new byte[1024];

    InputStream its;
    
    OutputStream ots;

    public SocketThread(User source, Map<String, User> clients) {
        this.source = source;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            its = source.getSocket().getInputStream();
            int len;
            while ((len = its.read(bytes)) > 0) {
                String content = new String(bytes, 0, len, StandardCharsets.UTF_8);
                if (!content.isEmpty()) {
                    String type = content.substring(0, 1);
                    switch (type) {
                        case Constants.TYPE_LOGIN:
                            login(content);
                            break;
                        case Constants.TYPE_LOGOUT:
                            logout();
                            break;
                        case Constants.TYPE_CONNECT:
                            connect(content);
                            break;
                        case Constants.TYPE_DISCONNECT:
                            disconnect();
                            break;
                        case Constants.TYPE_SEND:
                            send(content, len);
                            break;
                    }
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    
    private void login(String content) {
        String hostName = content.substring(2);
        source.setHostName(hostName);
        
        Set<String> keys = clients.keySet();
        for (String key : keys) {
            User user = clients.get(key);
            OutputStream ots = null;
            try {
                ots = user.getSocket().getOutputStream();
                ots.write(clients.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(ots);
            }
        }
    }
    
    private void logout() {
        String address = source.getAddress() + ":" + source.getPort();
        clients.remove(address);
        close();
    }
    
    private void connect(String content) {
        String key = content.substring(2);
        User user = clients.get(key);
        if (user != null) {
            target = user;
        }
    }
    
    private void disconnect() {
        
    }
    
    private void send(String content, int len) throws IOException {
        System.out.println(source.getHostName() + " " + source.getAddress() + ":" + source.getPort() + " -> "
                + target.getHostName() + " " + target.getAddress() + ":" + target.getPort()
                + " data: " + content);
        ots.write(bytes, 0, len);
    }
    
    private void close() {
        try {
            if (its != null) {
                its.close();
            }
            if (ots != null) {
                ots.write("3".getBytes());
                ots.close();
            }
            if (source != null) {
                source.getSocket().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        OutputStream ots = null;
        Set<String> keys = clients.keySet();
        for (String key : keys) {
            try {
                User user = clients.get(key);
                ots = user.getSocket().getOutputStream();
                if (ots != null) {
                    ots.write(clients.toString().getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(ots);
            }
        }
    }
    
    private void close(OutputStream ots) {
        if (ots != null) {
            try {
                ots.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
