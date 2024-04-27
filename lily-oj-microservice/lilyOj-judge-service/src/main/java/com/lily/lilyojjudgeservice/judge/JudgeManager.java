package com.lily.lilyojjudgeservice.judge;
import com.lily.lilyojcommon.common.LanguageEnum;
import com.lily.lilyojjudgeservice.strategy.DefaultJudgeStrategy;
import com.lily.lilyojjudgeservice.strategy.GolangJudgeStrategy;
import com.lily.lilyojjudgeservice.strategy.JavaJudgeStrategy;
import com.lily.lilyojjudgeservice.strategy.JudgeStrategy;
import com.lily.lilyojmodel.model.dto.judge.JudgeInfo;
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
        JudgeStrategy judgeStrategy;
        if (LanguageEnum.JAVA.getName().equalsIgnoreCase(language)){
            judgeStrategy = new JavaJudgeStrategy();
        } else if (LanguageEnum.GOLANG.getName().equalsIgnoreCase(language)) {
            judgeStrategy = new GolangJudgeStrategy();
        }else {
            judgeStrategy = new DefaultJudgeStrategy();
        }
        return judgeStrategy.doJudge(context);
    }
}
