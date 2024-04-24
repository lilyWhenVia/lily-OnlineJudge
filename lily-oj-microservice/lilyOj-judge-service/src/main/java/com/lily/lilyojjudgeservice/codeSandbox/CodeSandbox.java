package com.lily.lilyojjudgeservice.codeSandbox;

import com.lily.onlineJudge.judge.codeSandbox.model.dto.ExecuteCodeRequest;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

/**
 * 代码沙箱接口
 * Created by lily via on 2024/4/8 21:34
 */
@Service
public interface CodeSandbox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

}
