package com.lily.onlineJudge.judge.codeSandbox.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 代码沙箱参数返回类
 * Created by lily via on 2024/4/8 21:36
 */
@Data
public class ExecuteCodeResponse {

    /**
     * 用例输出列表
     */
    private List<String> codeOutput;

    /**
     * 接口执行信息
     */
    private String codeSandboxMes;

    /**
     * 沙箱执行状态
     */
    private Integer codeSandboxStatus;

    /**
     * 判题结果相关：判题信息
     */
    private JudgeInfo judgeInfo;
}
