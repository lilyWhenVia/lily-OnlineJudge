package com.lily.lilyojcommon.common;

/**
 * Created by lily via on 2024/4/8 22:14
 */
public enum SandboxErrorCode {


    SUCCESS(0, "执行成功"),
    PARAMS_ERROR(40000, "参数错误"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    SandboxErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
