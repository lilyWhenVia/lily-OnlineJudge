package com.lily.nativecodesandbox.controller;

import cn.hutool.http.HttpStatus;
import com.lily.nativecodesandbox.model.ExecuteCodeRequest;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import com.lily.nativecodesandbox.sandbox.CodeSandbox;
import com.lily.nativecodesandbox.sandbox.JavaSandbox.JavaCodeSandbox;
import com.lily.nativecodesandbox.sandbox.JavaSandbox.impl.JavaCodeSandboxImpl;
import com.lily.nativecodesandbox.sandbox.dockerSandbox.DockerCodeSandbox;
import com.lily.nativecodesandbox.sandbox.dockerSandbox.impl.DockerSandboxImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lily via on 2024/4/11 21:22
 */
@RestController("/")
public class codeController {

    @Resource
    private JavaCodeSandbox javaCodeSandbox;

    @Resource
    private DockerCodeSandbox dockerCodeSandbox;

    // 定义鉴权请求头和密钥
    @Value("${codeSandbox.auth.header}")
    private static final String AUTH_REQUEST_HEADER = "auth";

    @Value("${codeSandbox.auth.secret}")
    private static final String AUTH_REQUEST_SECRET = "secretKey";



    @PostMapping("/java")
    public ExecuteCodeResponse javaExecuteCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        if (! authCheck(request, response)){
            response.setStatus(HttpStatus.HTTP_FORBIDDEN);
            return null;
        }
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

    @PostMapping("/docker")
    public ExecuteCodeResponse dockerExecuteCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        if (! authCheck(request, response)){
            response.setStatus(HttpStatus.HTTP_FORBIDDEN);
            return null;
        }
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

    private Boolean authCheck(HttpServletRequest request, HttpServletResponse response) {
        // 基本的认证
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
            return false;
        }
        return true;
    }

}
