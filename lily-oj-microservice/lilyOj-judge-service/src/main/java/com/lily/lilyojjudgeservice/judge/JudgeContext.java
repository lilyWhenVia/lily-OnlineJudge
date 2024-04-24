package com.lily.lilyojjudgeservice.judge;

import com.lily.onlineJudge.judge.codeSandbox.model.dto.CodeOutput;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.JudgeInfo;
import com.lily.onlineJudge.model.entity.JudgeCase;
import com.lily.onlineJudge.model.entity.Question;
import com.lily.onlineJudge.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * Created by lily via on 2024/4/9 11:52
 */
@Data
public class JudgeContext {


    /**
     * 输入用例列表
     */
    private List<String> inputList;

    /**
     * 使用的编程语言
     */
    private String language;

    /**
     * 用例输出列表
     */
    private List<CodeOutput> codeOutput;

    /**
     * 用例输出列表
     */
    private List<String> standerOutput;

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

//     todo 可移除?
    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
