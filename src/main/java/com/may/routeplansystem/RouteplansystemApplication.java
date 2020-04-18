package com.may.routeplansystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.may.routeplansystem.dao")
@SpringBootApplication
public class RouteplansystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouteplansystemApplication.class, args);
    }
}
