package com.lily.lilyojjudgeservice.codeSandbox.CodeSandboxImpl;

import com.lily.lilyojjudgeservice.codeSandbox.CodeSandbox;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeRequest;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeResponse;
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
