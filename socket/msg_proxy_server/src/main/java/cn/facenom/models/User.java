package cn.facenom.models;

import lombok.Data;

import java.net.Socket;

/**
 * @author zyh
 * @version 1.0
 * @since 2023-03-05
 */
@Data
public class User {

    private String address;

    private int port;

    private String hostName;

    private Socket socket;

    public User() {
        
    }

    public User(String address, int port, Socket socket) {
        this.address = address;
        this.port = port;
        this.socket = socket;
    }
}
