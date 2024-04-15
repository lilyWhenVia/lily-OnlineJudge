package com.lily.nativecodesandbox.sandbox.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by lily via on 2024/4/15 12:11
 */
@SpringBootTest
class JavaCodeSandboxTest {


    public static void stopWatchTest(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("task1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        stopWatch.stop();
        long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
        System.out.printf("totalTimeMillis: %d\n", lastTaskTimeMillis);
    }

    public static void main(String[] args) {
        stopWatchTest();
    }
}