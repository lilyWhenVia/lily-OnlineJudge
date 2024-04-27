package com.lily.nativecodesandbox.controller;

import com.lily.nativecodesandbox.model.ExecuteCodeRequest;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import com.lily.nativecodesandbox.sandbox.CodeSandbox;
import com.lily.nativecodesandbox.sandbox.JavaSandbox.JavaCodeSandbox;
import com.lily.nativecodesandbox.sandbox.JavaSandbox.impl.JavaCodeSandboxImpl;
import com.lily.nativecodesandbox.sandbox.dockerSandbox.DockerCodeSandbox;
import com.lily.nativecodesandbox.sandbox.dockerSandbox.impl.DockerSandboxImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by lily via on 2024/4/11 21:22
 */
@RestController("/")
public class codeController {

    @Resource
    private JavaCodeSandbox javaCodeSandbox;

    @Resource
    private DockerCodeSandbox dockerCodeSandbox;


    @PostMapping("/javaCodeSandbox")
    public ExecuteCodeResponse javaExecuteCode(@RequestBody ExecuteCodeRequest executeCodeRequest) {
        // todo 鉴权
        String language = executeCodeRequest.getLanguage();
        ExecuteCodeResponse executeCodeResponse;
        if ("JAVA".equalsIgnoreCase(language)) {
            // todo 内部异常处理后统一返回携带异常信息的ExecuteCodeResponse
            executeCodeResponse = javaCodeSandbox.executeCode(executeCodeRequest);
        }else {
            // todo 调用可执行其他语言的沙箱
            return null;
        }
        return executeCodeResponse;
    }

    @PostMapping("/dockerCodeSandbox")
    public ExecuteCodeResponse dockerExecuteCode(@RequestBody ExecuteCodeRequest executeCodeRequest) {
        // todo 鉴权
        String language = executeCodeRequest.getLanguage();
        ExecuteCodeResponse executeCodeResponse;
        if ("JAVA".equalsIgnoreCase(language)) {
            // todo 内部异常处理后统一返回携带异常信息的ExecuteCodeResponse
            executeCodeResponse = dockerCodeSandbox.executeCode(executeCodeRequest);
        }else {
            // todo 调用可执行其他语言的沙箱
            return null;
        }
        return executeCodeResponse;
    }

}
