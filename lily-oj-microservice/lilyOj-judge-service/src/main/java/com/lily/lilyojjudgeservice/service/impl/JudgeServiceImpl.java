package com.lily.lilyojjudgeservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.lily.lilyojcommon.common.ErrorCode;
import com.lily.lilyojcommon.common.ExecuteStatusEnum;
import com.lily.lilyojcommon.common.LanguageEnum;
import com.lily.lilyojcommon.constant.StatusConstant;
import com.lily.lilyojcommon.exception.BusinessException;
import com.lily.lilyojcommon.exception.ThrowUtils;
import com.lily.lilyojjudgeservice.codeSandbox.CodeSandbox;
import com.lily.lilyojjudgeservice.codeSandbox.CodeSandboxFactory;
import com.lily.lilyojjudgeservice.codeSandbox.CodeSandboxProxy;
import com.lily.lilyojjudgeservice.judge.JudgeContext;
import com.lily.lilyojjudgeservice.judge.JudgeManager;
import com.lily.lilyojjudgeservice.service.JudgeService;
import com.lily.lilyojmodel.model.dto.judge.CodeOutput;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeRequest;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeResponse;
import com.lily.lilyojmodel.model.dto.judge.JudgeInfo;
import com.lily.lilyojmodel.model.dto.judge.JudgeCase;
import com.lily.lilyojmodel.model.entity.Question;
import com.lily.lilyojmodel.model.entity.QuestionSubmit;
import com.lily.lilyojserviceclient.service.QuestionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lily via on 2024/4/8 21:28
 */
@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {


//    @Resource
//    @Lazy
//    private QuestionSubmitFeignClient questionSubmitService;
    @Resource
    private QuestionFeignClient questionService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String codeSandboxType;

    @Override
    public void doJudge(Long questionSubmitId) {
//        1. 从数据查询提交记录，判断判题状态
        QuestionSubmit questionSubmit = questionService.getSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
//        2. 参数校验
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        Integer status = questionSubmit.getStatus();
        Long id = questionSubmit.getId(); // 用于回传
        Long questionId = questionSubmit.getQuestionId();
        ThrowUtils.throwIf(StringUtils.isEmpty(language), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(code), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(status == null || StatusConstant.SUCCEED == status, ErrorCode.PARAMS_ERROR);
        // 校验编程语言
        if (!LanguageEnum.isLanguageEnum(language)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "language参数错误");
        }
//        3. 更改判题状态为判题中
        QuestionSubmit submitStatus = new QuestionSubmit();
        submitStatus.setId(id);
        submitStatus.setStatus(StatusConstant.EXECUTE);
        questionService.updateById(submitStatus);
//        4. 查询根据questionId获取题目相关信息，构造代码沙箱运行需要参数
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "questionId不存在");
        }
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        // 4.1 获取全部的input集合
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 4.2 获取全部的output集合
        List<String> standerOutput = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest(inputList, language, code);
//        5. 调用代码沙箱服务，运行代码
        // 5.1 获得具体类型沙箱实例
        CodeSandbox codeSandbox = CodeSandboxFactory.getInstance(codeSandboxType);
        // 5.2 使用代理类增强沙箱实例的执行方法
        CodeSandboxProxy codeSandboxProxy = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeResponse executeCodeResponse = codeSandboxProxy.executeCode(executeCodeRequest);
//        5. 获取输出值
        List<CodeOutput> codeOutput = executeCodeResponse.getCodeOutput();
        String codeSandboxMes = executeCodeResponse.getCodeSandboxMes();
        Integer codeSandboxStatus = executeCodeResponse.getCodeSandboxStatus();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
//        6. 根据语言选择判题策略
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setInputList(inputList);
        judgeContext.setLanguage(language);
        judgeContext.setCodeOutput(codeOutput);
        judgeContext.setStanderOutput(standerOutput);
        judgeContext.setCodeSandboxMes(codeSandboxMes);
        judgeContext.setCodeSandboxStatus(codeSandboxStatus);
        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
//        6.1. 校验代码沙箱返回值
        JudgeInfo doneJudge = judgeManager.doJudge(judgeContext);
//        7. 判题状态更改， 判题结果存入数据库中
        QuestionSubmit succeedQueSub = new QuestionSubmit();
        succeedQueSub.setId(questionSubmitId);
        // 7.1 成功判题结束
        if (codeSandboxStatus == ExecuteStatusEnum.RUN_SUCCESS.getExecuteStatus()) {
            succeedQueSub.setStatus(StatusConstant.SUCCEED);
        } else {
            succeedQueSub.setStatus(StatusConstant.FAILED);
        }
        String infoString = JSONUtil.toJsonStr(doneJudge);
        succeedQueSub.setJudgeInfo(infoString);
        boolean b = questionService.updateById(succeedQueSub);
        // todo
        if (!b) {
            log.error("QuestionSubmit SUCCEED status update failed");
            throw new RuntimeException("QuestionSubmit SUCCEED status update failed");
        }
    }
}
