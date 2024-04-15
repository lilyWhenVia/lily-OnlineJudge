package com.lily.onlineJudge.model.dto.question;

import com.lily.onlineJudge.model.entity.JudgeCase;
import com.lily.onlineJudge.model.entity.JudgeConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建请求
 *
* @author lily <a href="https://github.com/lilyWhenVia">作者github</a>
 */
@Data
public class QuestionAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 题目属性标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;


    /**
     * 判题用例（json数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 题目属性（json对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 创建用户 id
     */
    private Long userId;


    private static final long serialVersionUID = -5358887774346999192L;
}