package com.lily.onlineJudge.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.lily.onlineJudge.judge.JudgeContext;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.JudgeInfo;
import com.lily.onlineJudge.model.entity.JudgeCase;
import com.lily.onlineJudge.model.entity.JudgeConfig;
import com.lily.onlineJudge.model.entity.Question;
import com.lily.onlineJudge.model.entity.QuestionSubmit;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 默认判题策略
 * Created by lily via on 2024/4/9 11:46
 */
public class DefaultJudgeStrategy implements JudgeStrategy{
    @Override
    public JudgeInfo doJudge(JudgeContext context) {
        // 1.
        List<String> inputList = context.getInputList();
        String language = context.getLanguage();
        List<String> codeOutput = context.getCodeOutput();
        String codeSandboxMes = context.getCodeSandboxMes();
        Integer codeSandboxStatus = context.getCodeSandboxStatus();
        JudgeInfo judgeInfo = context.getJudgeInfo();
        List<JudgeCase> judgeCaseList = context.getJudgeCaseList();
        List<String> standerOutput = context.getStanderOutput();
        Question question = context.getQuestion();
        QuestionSubmit questionSubmit = context.getQuestionSubmit();

        // 1.1. 确认沙箱正确执行完毕
        if (!codeSandboxMes.equals("succeed")){
            throw new RuntimeException("代码执行异常： {}"+codeSandboxMes);
        }
        // 1.2根据代码沙箱返回值设置校验逻辑
        if (codeSandboxStatus==2){
            throw new RuntimeException("代码执行异常： {}"+codeSandboxStatus);
        }
        // 2. 校验运行参数限制
        String judgeConfig = question.getJudgeConfig();
        JudgeConfig configBean = JSONUtil.toBean(judgeConfig, JudgeConfig.class);
        Long timeLimit = configBean.getTimeLimit();
        Long memoryLimit = configBean.getMemoryLimit();
        Long stackLimit = configBean.getStackLimit();
        // 2.1 info
        String message = judgeInfo.getMessage();
        Long time = judgeInfo.getTime();
        Long memory = judgeInfo.getMemory();
        Long stack = judgeInfo.getStack();

        if (timeLimit>time){
            throw new RuntimeException("运行超时");
        }
        if (memoryLimit>memory){
            throw new RuntimeException("内存超限");
        }
        if (stackLimit>stack){
            throw new RuntimeException("栈溢出");
        }
        // 3. 检查输出
        for (int i = 0; i < standerOutput.size(); i++) {
            for (int j = 0; j < codeOutput.size(); j++) {
                if (!StringUtils.equals(standerOutput.get(i),codeOutput.get(j))){
                    // todo
                    throw new RuntimeException("第"+j+"个输出有误");
                }
            }
        }
        // todo
        return new JudgeInfo("succeed", time, memory, stack);
    }
}
