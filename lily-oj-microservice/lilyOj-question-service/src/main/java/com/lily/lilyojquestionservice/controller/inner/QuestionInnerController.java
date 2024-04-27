package com.lily.lilyojquestionservice.controller.inner;

import com.lily.lilyojmodel.model.entity.Question;
import com.lily.lilyojmodel.model.entity.QuestionSubmit;
import com.lily.lilyojquestionservice.service.QuestionService;
import com.lily.lilyojquestionservice.service.QuestionSubmitService;
import com.lily.lilyojserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by lily via on 2024/4/26 10:56
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/getById")
    public Question getById(@RequestParam("questionId") Long questionId) {
        return questionService.getById(questionId);
    }

    @Override
    @GetMapping("/submit_getById")
    public QuestionSubmit getSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @Override
    @PostMapping("/submit_update")
    public Boolean updateById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }
}
