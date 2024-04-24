package com.lily.onlineJudge.model.dto.question;

import com.lily.onlineJudge.model.entity.JudgeCase;
import com.lily.onlineJudge.model.entity.JudgeConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
* @author lily <a href="https://github.com/lilyWhenVia">come to find lily</a>
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;


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


    private static final long serialVersionUID = -5725504464201694050L;
}