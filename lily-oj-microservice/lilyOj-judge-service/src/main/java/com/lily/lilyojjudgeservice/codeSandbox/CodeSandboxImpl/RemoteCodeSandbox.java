package com.lily.lilyojjudgeservice.codeSandbox.CodeSandboxImpl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.lily.lilyojcommon.common.ErrorCode;
import com.lily.lilyojcommon.common.ExecuteStatusEnum;
import com.lily.lilyojcommon.common.SandboxErrorCode;
import com.lily.lilyojjudgeservice.codeSandbox.CodeSandbox;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeRequest;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 调用自实现的远程Docker代码沙箱
 * Created by lily via on 2024/4/8 21:54
 */
@Service
public class RemoteCodeSandbox implements CodeSandbox {

    // 定义鉴权请求头和密钥
    @Value("${codeSandbox.auth.header}")
    private static final String AUTH_REQUEST_HEADER = "auth";

    @Value("${codeSandbox.auth.secret}")
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Value("${codesandbox.url}")
    private String remoteCodeSandboxUrl = "http://localhost:8081/java";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("RemoteCodeSandbox...");
        HttpResponse response = HttpUtil.createPost(remoteCodeSandboxUrl)
                .header("Content-Type", "application/json")
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(JSONUtil.toJsonStr(executeCodeRequest))
                .execute();
        // 获取返回结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        if (response.getStatus() == HttpStatus.NOT_FOUND.value()){
            executeCodeResponse.setCodeSandboxMes(SandboxErrorCode.CODESANDBOX_NOT_FOUND.getMessage());
            executeCodeResponse.setCodeSandboxStatus(SandboxErrorCode.CODESANDBOX_NOT_FOUND.getCode());
            return executeCodeResponse;
        } else if (response.getStatus() == HttpStatus.FORBIDDEN.value()){
            executeCodeResponse.setCodeSandboxMes(SandboxErrorCode.FORBIDDEN_ERROR.getMessage());
            executeCodeResponse.setCodeSandboxStatus(SandboxErrorCode.FORBIDDEN_ERROR.getCode());
            return executeCodeResponse;
        }
        String body = response.body();
        if (body==null || StringUtils.isEmpty(body)){
            executeCodeResponse.setCodeSandboxMes(SandboxErrorCode.SYSTEM_ERROR.getMessage());
            executeCodeResponse.setCodeSandboxStatus(SandboxErrorCode.SYSTEM_ERROR.getCode());
            return executeCodeResponse;
        }
        executeCodeResponse = JSONUtil.toBean(body, ExecuteCodeResponse.class);
        return executeCodeResponse;
    }
}
