package com.lily.lilyojquestionservice.controller.inner;

import com.lily.lilyojmodel.model.entity.QuestionSubmit;
import com.lily.lilyojquestionservice.service.QuestionSubmitService;
import com.lily.lilyojserviceclient.service.QuestionSubmitFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by lily via on 2024/4/26 11:01
 */
//@RestController
//@RequestMapping("/submit/inner")
@Deprecated
public class QuestionSubmitInnerController implements QuestionSubmitFeignClient {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/getById")
    public QuestionSubmit getById(@RequestParam("questionSubmitId") Long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @Override
    @PostMapping("/update")
    public Boolean updateById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }
}
