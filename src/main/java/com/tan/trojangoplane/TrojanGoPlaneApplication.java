package com.tan.trojangoplane;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrojanGoPlaneApplication {

    @Value("${trojanserver.host}")
    private static final String host =null;
    @Value("${trojanserver.port}")
    private static final int port = 0;

    public static void main(String[] args) {
        SpringApplication.run(TrojanGoPlaneApplication.class, args);
        System.out.println("服务器："+host+port);

    }

}
