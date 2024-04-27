package com.lily.lilyojserviceclient.service;
import com.lily.lilyojmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by lily via on 2024/4/26 9:51
 */
//@FeignClient(name = "lilyOj-question-service", path = "/api/question/submit/inner")
@Deprecated
public interface QuestionSubmitFeignClient {

    // questionSubmitService
    @GetMapping("/getById")
    QuestionSubmit getById(@RequestParam("questionSubmitId") Long questionSubmitId);

    @PostMapping("/update")
    Boolean updateById(@RequestBody QuestionSubmit questionSubmit);
}
