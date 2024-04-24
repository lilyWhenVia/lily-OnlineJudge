package com.lily.lilyojmodel.model.vo;

import com.lily.onlineJudge.model.entity.JudgeInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目提交结果表
 *
 * @TableName question_submit
 */
@Data
public class QuestionSubmitVO implements Serializable {
    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 使用的编程语言
     */
    private String language;

    /**
     * 提交代码
     */
    private String code;

    /**
     * 判题状态（0-待判题，1-判题中，2-失败，3-成功）
     */
    private Integer status;

    /**
     * 判题信息（json对象）
     */
    private JudgeInfo judgeInfo;

    /**
     * 创建题目人的信息
     */
    private UserVO userVO;


    /**
     * 题目的信息
     */
    private QuestionVO questionVO;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 6482519624986127237L;


}