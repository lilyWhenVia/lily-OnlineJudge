package com.lily.nativecodesandbox.dto;

import lombok.Data;

/**
 * Created by lily via on 2024/4/14 22:40
 */
/**
 * 进程执行信息
 */
@Data
public class ExecuteMessage {

    private Integer exitValue;

    private String message;

    private String errorMessage;

    private Long time;
}


