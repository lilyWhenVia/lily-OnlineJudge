package com.lily.onlineJudge.judge.impl;

import cn.hutool.json.JSONUtil;
import com.lily.onlineJudge.common.ErrorCode;
import com.lily.onlineJudge.exception.BusinessException;
import com.lily.onlineJudge.judge.JudgeContext;
import com.lily.onlineJudge.judge.JudgeManager;
import com.lily.onlineJudge.judge.codeSandbox.CodeSandbox;
import com.lily.onlineJudge.judge.codeSandbox.CodeSandboxFactory;
import com.lily.onlineJudge.judge.codeSandbox.CodeSandboxProxy;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.ExecuteCodeRequest;
import com.lily.onlineJudge.model.entity.JudgeCase;
import com.lily.onlineJudge.model.entity.Question;
import com.lily.onlineJudge.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lily via on 2024/4/9 10:56
 */
@SpringBootTest
class JudgeServiceImplTest {

    @Resource
    QuestionService questionService;

    @Resource
    JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String codeSandboxType;

    @Test
    public void doJudgeTest(){

        Question question = questionService.getById(1777183002560950273L);
        if (question == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "questionId不存在");
        }
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        JudgeCase judgeCase1 = new JudgeCase();
        judgeCase1.setInput("2 2");
        judgeCase1.setOutput("3 3");

        judgeCaseList.add(judgeCase1);
//        for (JudgeCase aCase : judgeCaseList) {
//            System.out.println(aCase);
//        }

        List<String> collect = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void typeTest(){
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest(null, "JAVA", null);
        // 5.1 获得具体类型沙箱实例
        CodeSandbox codeSandbox = CodeSandboxFactory.getInstance(codeSandboxType);
        // 5.2 使用代理类增强沙箱实例的执行方法
        CodeSandboxProxy codeSandboxProxy = new CodeSandboxProxy(codeSandbox);
        codeSandboxProxy.executeCode(executeCodeRequest);
//        6. 根据语言选择判题策略
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setLanguage("JAVA");
        judgeManager.doJudge(judgeContext);

    }
}