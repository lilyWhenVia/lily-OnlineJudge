package com.lily.lilyojserviceclient.service;

import com.lily.lilyojmodel.model.entity.Question;
import com.lily.lilyojmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
* @author lily
* @createDate 2024-04-07 23:42:03
*/
@FeignClient(name = "lilyOj-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {

    /**
     * 获取题目
     *
     * @param questionId 题目id
     * @return 题目
     */
    @GetMapping("/getById")
    Question getById(@RequestParam("questionId") Long questionId);


    // questionSubmitService
    @GetMapping("/submit_getById")
    QuestionSubmit getSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId);

    @PostMapping("/submit_update")
    Boolean updateById(@RequestBody QuestionSubmit questionSubmit);

}
