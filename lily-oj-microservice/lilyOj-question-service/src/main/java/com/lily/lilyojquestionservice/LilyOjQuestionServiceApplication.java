package com.lily.lilyojquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lily.lilyojquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan(basePackages = "com.lily")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lily.lilyojquestionservice.controller.inner")
public class LilyOjQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LilyOjQuestionServiceApplication.class, args);
    }

}
