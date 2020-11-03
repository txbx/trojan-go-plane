package com.tan.trojangoplane.model.pojo;

/**
 * @author : tan
 * @date: 2020/11/3 13:18
 * @description :服务器
 */
public class Server {
    private Integer id;
    private String ip;
    private Integer port;
    //总流量（上传下载）
    private Long serversumdown;
    private Long serversumupload;
    //总用户（写会员层面的时候加入）
}