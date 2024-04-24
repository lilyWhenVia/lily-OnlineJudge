package com.lily.lilyojjudgeservice.codeSandbox.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 代码沙箱执行状态枚举类
 * Created by lily via on 2024/4/15 8:50
 */
@AllArgsConstructor
@Getter
public enum ExecuteStatusEnum {

    /**
     * 执行成功
     */
    RUN_SUCCESS(0, "执行成功"),

    /**
     * 执行失败
     */
    RUN_FAIL(1, "执行失败"),

    /**
     * 编译成功
     */
    COMPILE_SUCCESS(2, "编译成功"),

    /**
     * 编译失败
     */
    COMPILE_FAIL(3, "编译失败"),

    /**
     * 运行超时
     */
    RUN_TIMEOUT(4, "运行超时"),

    /**
     * 内存超限
     */
    MEMORY_LIMIT_EXCEEDED(5, "内存超限"),

    /**
     * 系统异常
     */
     SYSTEM_ERROR(6, "系统异常");

    private final int executeStatus;

    private final String statusName;
}
