package com.lily.onlineJudge.judge.codeSandbox;

import com.lily.onlineJudge.judge.codeSandbox.model.dto.ExecuteCodeRequest;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.ExecuteCodeResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱的代理类
 * Created by lily via on 2024/4/9 9:58
 */
@Data
@Slf4j
public class CodeSandboxProxy implements CodeSandbox{

    /**
     * 代理类初始化一次代码沙箱实例
     */
    public final CodeSandbox codeSandbox;

    /**
     * 构造器初始化代码沙箱
     */
    public CodeSandboxProxy(CodeSandbox codeSandbox){
        this.codeSandbox = codeSandbox;
    }

    /**
     * 增强executeCode方法
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("before start executeCode...");
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("end up executeCode...");
        return executeCodeResponse;
    }
}
