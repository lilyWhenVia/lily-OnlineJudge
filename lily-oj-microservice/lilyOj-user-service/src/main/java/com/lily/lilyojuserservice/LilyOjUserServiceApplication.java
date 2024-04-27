package com.lily.lilyojuserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lily.lilyojuserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan(basePackages = "com.lily")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lily.lilyojuserservice.service")
public class LilyOjUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LilyOjUserServiceApplication.class, args);
    }

}
