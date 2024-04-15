package com.lily.onlineJudge.judge.codeSandbox.CodeSandboxImpl;

import com.lily.onlineJudge.judge.codeSandbox.CodeSandbox;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.ExecuteCodeRequest;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

/**
 * Created by lily via on 2024/4/8 21:55
 */
@Service
public class ThirdPartyCodeSandbox implements CodeSandbox {


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("ThirdPartyCodeSandbox...");
        return null;
    }
}
