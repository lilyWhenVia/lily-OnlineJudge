package com.lily.lilyojjudgeservice.codeSandbox.model.dto;

import lombok.Data;

/**
 * 代码实际执行内容的输出
 * Created by lily via on 2024/4/14 11:09
 */
@Data
public class CodeOutput {
    /**
     * 用例正确输出结果
     */
    private String stdoutMessage;

    /**
     * 用例错误输出信息
     */
    private String stdErrorMessage;

    /**
     * 输入用例
     */
    private String inputExample;
}
