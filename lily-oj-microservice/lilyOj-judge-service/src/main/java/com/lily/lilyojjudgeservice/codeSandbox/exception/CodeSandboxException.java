package com.lily.lilyojjudgeservice.codeSandbox.exception;

import com.lily.onlineJudge.judge.codeSandbox.common.SandboxErrorCode;

/**
 * 代码沙箱自定义异常类
 * Created by lily via on 2024/4/8 22:12
 */
public class CodeSandboxException extends RuntimeException{

    /**
     * 错误码
     */
    private final int code;

    public CodeSandboxException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CodeSandboxException(SandboxErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public CodeSandboxException(SandboxErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
