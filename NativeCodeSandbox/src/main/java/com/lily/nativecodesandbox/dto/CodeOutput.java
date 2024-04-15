package com.lily.nativecodesandbox.dto;

import lombok.Data;

/**
 * 代码实际执行内容的输出
 * Created by lily via on 2024/4/14 11:09
 */
@Data
public class CodeOutput {
    private String stdoutMessage;
    private String stdErrorMessage;
    private String codeExecute;
}
