package com.tan.trojangoplane;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@MapperScan(basePackages = "com.tan.trojangoplane.mapper")
public class TrojanGoPlaneApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrojanGoPlaneApplication.class, args);
    }

}
