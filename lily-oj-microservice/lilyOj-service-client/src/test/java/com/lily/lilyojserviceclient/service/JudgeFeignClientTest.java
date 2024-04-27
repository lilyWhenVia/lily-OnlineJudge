package com.lily.lilyojserviceclient.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by lily via on 2024/4/27 10:57
 */
@SpringBootTest
class JudgeFeignClientTest {

    @Resource
    private JudgeFeignClient judgeFeignClient;

    @Test
    void doJudge() {
        judgeFeignClient.doJudge(1784054138867904514L);
    }
}