package com.lily.lilyojmodel.model.dto.judge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码沙箱参数返回类
 * Created by lily via on 2024/4/8 21:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 用例输出列表
     */
    private List<CodeOutput> codeOutput;

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
