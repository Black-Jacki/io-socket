package cn.facenom.models;

import lombok.Data;

/**
 * @author zyh
 * @version 1.0
 * @since 2023-03-05
 */
@Data
public class Message {

    private String address;

    private String port;

    private String host;

    private String content;
}
