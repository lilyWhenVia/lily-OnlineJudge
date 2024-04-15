package com.lily.onlineJudge.judge;

import com.lily.onlineJudge.common.LanguageEnum;
import com.lily.onlineJudge.judge.codeSandbox.model.dto.JudgeInfo;
import com.lily.onlineJudge.judge.strategy.DefaultJudgeStrategy;
import com.lily.onlineJudge.judge.strategy.JavaJudgeStrategy;
import com.lily.onlineJudge.judge.strategy.JudgeStrategy;
import org.springframework.stereotype.Service;


/**
 * 根据不同的语言选择实现不同的判题策略
 * Created by lily via on 2024/4/9 11:54
 */
@Service
public class JudgeManager implements JudgeStrategy {


    @Override
    public JudgeInfo doJudge(JudgeContext context) {
        String language = context.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (LanguageEnum.JAVA.getName().equals(language)){
            judgeStrategy = new JavaJudgeStrategy();
        }
        return judgeStrategy.doJudge(context);
    }
}
