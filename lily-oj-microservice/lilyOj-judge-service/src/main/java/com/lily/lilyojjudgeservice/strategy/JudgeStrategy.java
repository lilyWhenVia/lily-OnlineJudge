package com.lily.lilyojjudgeservice.strategy;

import com.lily.lilyojjudgeservice.judge.JudgeContext;
import com.lily.lilyojmodel.model.dto.judge.JudgeInfo;

/**
 * Created by lily via on 2024/4/9 11:48
 */
public interface JudgeStrategy {
    JudgeInfo doJudge(JudgeContext context);
}
