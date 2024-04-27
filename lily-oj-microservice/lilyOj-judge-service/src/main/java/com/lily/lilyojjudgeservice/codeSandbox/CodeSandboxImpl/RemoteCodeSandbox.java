package com.lily.lilyojjudgeservice.codeSandbox.CodeSandboxImpl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.lily.lilyojcommon.common.ErrorCode;
import com.lily.lilyojjudgeservice.codeSandbox.CodeSandbox;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeRequest;
import com.lily.lilyojmodel.model.dto.judge.ExecuteCodeResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 调用自实现的远程Docker代码沙箱
 * Created by lily via on 2024/4/8 21:54
 */
@Service
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("RemoteCodeSandbox...");
        HttpResponse response = HttpUtil.createPost("http://localhost:8080/codeSandbox")
                .body(JSONUtil.toJsonStr(executeCodeRequest))
                .execute();
        if (response.getStatus() != 200){
            return new ExecuteCodeResponse(null, "调用代码沙箱失败", ErrorCode.SYSTEM_ERROR.getCode(), null);
        }
        String body = response.body();
        if (StringUtils.isEmpty(body)){
            // todo 异常处理，更改数据库信息
            return new ExecuteCodeResponse(null, "调用代码沙箱失败",ErrorCode.SYSTEM_ERROR.getCode(), null);
        }
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(body, ExecuteCodeResponse.class);
        return executeCodeResponse;
    }
}
