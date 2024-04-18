package com.lily.nativecodesandbox.controller;

import com.lily.nativecodesandbox.JavaSandbox.JavaCodeSandboxImpl;
import com.lily.nativecodesandbox.model.ExecuteCodeRequest;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import com.lily.nativecodesandbox.sandbox.CodeSandbox;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lily via on 2024/4/11 21:22
 */
@RestController("/")
public class codeController {

    @Resource
    private CodeSandbox javaCodeSandboxImpl;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, 世界!";
    }

    @PostMapping("/codeSandbox")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest) {
        // todo 鉴权
        String language = executeCodeRequest.getLanguage();
        ExecuteCodeResponse executeCodeResponse;
        if ("JAVA".equals(language)) {
            // todo 内部异常处理后统一返回携带异常信息的ExecuteCodeResponse
            executeCodeResponse = javaCodeSandboxImpl.executeCode(executeCodeRequest);
        }else {
            // todo 调用可执行其他语言的沙箱
            return null;
        }
        return executeCodeResponse;
    }

}
