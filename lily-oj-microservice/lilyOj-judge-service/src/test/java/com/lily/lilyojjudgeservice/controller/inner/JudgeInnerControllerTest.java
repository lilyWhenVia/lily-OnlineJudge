package com.lily.lilyojjudgeservice.controller.inner;

import com.lily.lilyojserviceclient.service.JudgeFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by lily via on 2024/4/27 15:01
 */
@SpringBootTest
class JudgeInnerControllerTest {


    @Resource
    JudgeFeignClient judgeFeignClient;

    @Test
    void doJudge() {
        judgeFeignClient.doJudge(1784064511553622017L);
        System.out.println("JudgeInnerControllerTest.doJudge");
    }
}