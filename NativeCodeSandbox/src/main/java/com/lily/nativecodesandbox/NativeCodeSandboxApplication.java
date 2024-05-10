package com.lily.nativecodesandbox;

import com.lily.nativecodesandbox.Once.FirstInitJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.lily")
public class NativeCodeSandboxApplication {

    private static final Logger log = LoggerFactory.getLogger(NativeCodeSandboxApplication.class);

    public static void main(String[] args) {
        try {
//            FirstInitJob.InitContainer();
        } catch (Exception e) {
            log.error("初始化Docker容器失败", e);
            throw new RuntimeException(e);
        } finally {
            SpringApplication.run(NativeCodeSandboxApplication.class, args);
        }
    }

}
