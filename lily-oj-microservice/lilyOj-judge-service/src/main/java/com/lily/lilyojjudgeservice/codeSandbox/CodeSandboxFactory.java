package com.lily.lilyojjudgeservice.codeSandbox;

import com.lily.lilyojcommon.common.SandboxErrorCode;
import com.lily.lilyojcommon.exception.CodeSandboxException;
import com.lily.lilyojjudgeservice.codeSandbox.CodeSandboxImpl.ExampleCodeSandbox;
import com.lily.lilyojjudgeservice.codeSandbox.CodeSandboxImpl.RemoteCodeSandbox;
import com.lily.lilyojjudgeservice.codeSandbox.CodeSandboxImpl.ThirdPartyCodeSandbox;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 根据配置调用不同代码沙箱模块服务的沙箱工厂
 * Created by lily via on 2024/4/8 21:59
 */
@Data
@Slf4j
@Service
public class CodeSandboxFactory {


    public static CodeSandbox getInstance(String codeSandboxType) {
        switch (codeSandboxType) {
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            case "example":
                return new ExampleCodeSandbox();
            case "Remote":
                return new RemoteCodeSandbox();
        }
        log.info("codeSandbox type not found");
        throw new CodeSandboxException(SandboxErrorCode.PARAMS_ERROR);
    }
}
