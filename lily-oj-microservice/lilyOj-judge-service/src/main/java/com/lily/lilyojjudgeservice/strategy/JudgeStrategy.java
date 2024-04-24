package com.lily.lilyojjudgeservice.strategy;

import com.lily.onlineJudge.judge.JudgeContext;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.JudgeInfo;

/**
 * Created by lily via on 2024/4/9 11:48
 */
public interface JudgeStrategy {
    public JudgeInfo doJudge(JudgeContext context);
}
