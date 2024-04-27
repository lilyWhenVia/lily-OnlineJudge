package com.lily.lilyojjudgeservice.codeSandbox;

import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeRequest;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

/**
 * 代码沙箱接口
 * Created by lily via on 2024/4/8 21:34
 */
@Service
public interface CodeSandbox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

}
