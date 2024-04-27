package com.lily.lilyojserviceclient.service;

import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by lily via on 2024/4/27 8:22
 */
@SpringBootTest
class QuestionFeignClientTest {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Test
    void getById() {
        HttpUtil.createGet("http://localhost:8080//inner/getById").execute();
        System.out.println("QuestionFeignClientTest.getById");
    }

    @Test
    void testGetById() {
        questionFeignClient.getSubmitById(1784064511553622017L);
        System.out.println("QuestionFeignClientTest.testGetById");
    }

    @Test
    void testSubmit() {
        questionFeignClient.getById(1784054138867904514L);
        System.out.println("sbumit.testGetById");
    }
}