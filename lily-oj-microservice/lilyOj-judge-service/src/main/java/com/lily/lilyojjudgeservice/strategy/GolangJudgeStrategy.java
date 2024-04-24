package com.lily.lilyojjudgeservice.strategy;

import cn.hutool.json.JSONUtil;
import com.lily.onlineJudge.judge.JudgeContext;
import com.lily.onlineJudge.judge.codeSandbox.common.ExecuteStatusEnum;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.CodeOutput;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.JudgeInfo;
import com.lily.onlineJudge.model.entity.JudgeConfig;
import com.lily.onlineJudge.model.entity.Question;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lily via on 2024/4/9 11:46
 */
public class GolangJudgeStrategy implements JudgeStrategy{
    @Override
    public JudgeInfo doJudge(JudgeContext context) {
        // 1.
        List<CodeOutput> codeOutput = context.getCodeOutput();
        String codeSandboxMes = context.getCodeSandboxMes();
        Integer codeSandboxStatus = context.getCodeSandboxStatus();
        JudgeInfo judgeInfo = context.getJudgeInfo();
        List<String> standerOutput = context.getStanderOutput();
        Question question = context.getQuestion();

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

        // 1.1. 判断沙箱是否异常
        if (ExecuteStatusEnum.RUN_SUCCESS.getExecuteStatus() == codeSandboxStatus) {// 1.2根据代码沙箱返回值设置校验逻辑
            List<String> errorMessage = codeOutput.stream().map(CodeOutput::getStdErrorMessage).collect(Collectors.toList());
            // 检查是否有运行错误
            if (errorMessage.isEmpty()) {
                if (timeLimit > time) {
                    return new JudgeInfo("运行超时", time, memory, stack);
                }
                if (memoryLimit > memory) {
                    return new JudgeInfo("内存超限", time, memory, stack);
                }
                if (stackLimit > stack) {
                    return new JudgeInfo("栈溢出", time, memory, stack);
                }
                // 3. 检查输出
                for (int i = 0; i < standerOutput.size(); i++) {
                    for (int j = 0; j < codeOutput.size(); j++) {
                        if (!StringUtils.equals(standerOutput.get(i), codeOutput.get(j).getStdoutMessage())) {
                            // todo
                            return new JudgeInfo("第" + j + "个输出有误", time, memory, stack);
                        }
                    }
                }
                // todo
                return new JudgeInfo("succeed", time, memory, stack);
            } else {
                return new JudgeInfo("执行异常：" + errorMessage, time, memory, stack);
            }
        } else {
            // 编译失败，运行失败等
            return new JudgeInfo(codeSandboxMes, time, memory, stack);
        }
    }
}
