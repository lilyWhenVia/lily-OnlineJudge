package com.lily.lilyojjudgeservice.codeSandbox.CodeSandboxImpl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by lily via on 2024/4/29 22:49
 */
class RemoteCodeSandboxTest {

    // 定义鉴权请求头和密钥
    @Value("${codeSandbox.auth.header}")
    private static final String AUTH_REQUEST_HEADER = "auth";

    @Value("${codeSandbox.auth.secret}")
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Value("${codesandbox.url}")
    private String remoteCodeSandboxUrl = "http://localhost:8080/java";

    @Test
    void executeCode() {

    }

    public static void main(String[] args) {
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(new ArrayList<>());
        executeCodeRequest.setLanguage("JAVA");
        executeCodeRequest.setCode("aaaa");
        HttpResponse response = HttpUtil.createPost("http://localhost:8080/docker")
                .header("Content-Type", "application/json")
                .header(AUTH_REQUEST_HEADER, "#Fc")
                .body(JSONUtil.toJsonStr(executeCodeRequest))
                .execute();
        System.out.println(response.body());
    }
}