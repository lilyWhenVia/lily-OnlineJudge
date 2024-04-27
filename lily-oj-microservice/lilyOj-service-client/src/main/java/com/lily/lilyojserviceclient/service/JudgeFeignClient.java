package com.lily.lilyojserviceclient.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 定义了判题的方法
 * Created by lily via on 2024/4/8 21:26
 */
@FeignClient(name = "lilyOj-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient{

    /**
     * 判题方法
     * @param questionSubmitId 题目提交id
     */
    @PostMapping("/do")
    Boolean doJudge(@RequestBody Long questionSubmitId);
}
