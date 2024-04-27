package com.lily.lilyojjudgeservice.strategy;

import cn.hutool.json.JSONUtil;
import com.lily.lilyojcommon.common.ExecuteStatusEnum;
import com.lily.lilyojjudgeservice.judge.JudgeContext;
import com.lily.lilyojmodel.model.dto.judge.CodeOutput;
import com.lily.lilyojmodel.model.dto.judge.JudgeConfig;
import com.lily.lilyojmodel.model.dto.judge.JudgeInfo;
import com.lily.lilyojmodel.model.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by lily via on 2024/4/9 11:46
 */
@Slf4j
public class JavaJudgeStrategy implements JudgeStrategy{

    @Override
    public JudgeInfo doJudge(JudgeContext context) {
        // 1.
        List<CodeOutput> codeOutput = context.getCodeOutput();
        String codeSandboxMes = context.getCodeSandboxMes();
        JudgeInfo judgeInfo = context.getJudgeInfo();
        List<String> standerOutput = context.getStanderOutput();
        Question question = context.getQuestion();

        if (codeOutput == null || codeOutput.isEmpty()) {
            return new JudgeInfo(codeSandboxMes, 0L, 0L, 0L);
        }
        if (judgeInfo == null) {
            return new JudgeInfo(codeSandboxMes, 0L, 0L, 0L);
        }
        // 2. 校验运行参数限制
        String judgeConfig = question.getJudgeConfig();
        JudgeConfig configBean = JSONUtil.toBean(judgeConfig, JudgeConfig.class);
        Long timeLimit = configBean.getTimeLimit();
        Long memoryLimit = configBean.getMemoryLimit();
        Long stackLimit = configBean.getStackLimit();

        // 2.1 info  todo 判空校验
        String message = judgeInfo.getMessage();
        Long time = judgeInfo.getTime();
        Long memory = judgeInfo.getMemory();
        Long stack = judgeInfo.getStack();

        // 1.1. 判断沙箱运行是否异常
        if (StringUtils.equals(ExecuteStatusEnum.RUN_SUCCESS.getStatusName(), message)) {// 1.2根据代码沙箱返回值设置校验逻辑

            List<String> errorMessage = codeOutput.stream().map(CodeOutput::getStdErrorMessage).collect(Collectors.toList());
            // 检查执行过程中是否有用例错误  errorMessage全是null
            boolean allNull = errorMessage.stream().allMatch(Objects::isNull);
            if (allNull) {
                if (timeLimit-10 < time) {
                    return new JudgeInfo(ExecuteStatusEnum.RUN_TIMEOUT.getStatusName(), time, memory, stack);
                }
                if (memoryLimit < memory) {
                    return new JudgeInfo(ExecuteStatusEnum.MEMORY_LIMIT_EXCEEDED.getStatusName(), time, memory, stack);
                }
                if (stackLimit < stack) {
                    return new JudgeInfo(ExecuteStatusEnum.RUN_TIMEOUT.getStatusName(), time, memory, stack);
                }
                // 3. 检查输出
                if (standerOutput.size() != codeOutput.size()) {
                    return new JudgeInfo("输出个数不一致", time, memory, stack);
                }
                for (int i = 0; i < standerOutput.size(); i++) {
                        if (!StringUtils.equals(standerOutput.get(i), codeOutput.get(i).getStdoutMessage())) {
                            // todo
                            return new JudgeInfo("第" + (i+1) + "个输出有误。用例输出"+codeOutput.get(i), time, memory, stack);
                    }
                }
                // todo
                return new JudgeInfo(ExecuteStatusEnum.RUN_SUCCESS.getStatusName(), time, memory, stack);
            } else {
                return new JudgeInfo(ExecuteStatusEnum.RUN_FAIL.getStatusName() +" :"+ errorMessage, time, memory, stack);
            }
        } else {
            log.error("codeSandboxMes:{}", codeSandboxMes);
            // 编译失败，运行失败等
            return new JudgeInfo(codeSandboxMes, time, memory, stack);
        }
    }
}
