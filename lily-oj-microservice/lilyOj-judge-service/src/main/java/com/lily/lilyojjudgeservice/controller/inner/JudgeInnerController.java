package com.lily.lilyojjudgeservice.controller.inner;

import com.lily.lilyojjudgeservice.service.JudgeService;
import com.lily.lilyojserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by lily via on 2024/4/26 11:16
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient{

    @Resource
    private JudgeService judgeService;

    @Override
    @PostMapping("/do")
    public Boolean doJudge(@RequestBody Long questionSubmitId) {
        judgeService.doJudge(questionSubmitId);
        return null;
    }
}
