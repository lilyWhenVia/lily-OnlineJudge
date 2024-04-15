package com.lily.onlineJudge.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 题目提交结果表
 * @TableName question_submit
 */
@Data
public class QuestionSubmit implements Serializable {
    /**
     * id
     */
    private Long id;

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
     * 提交状态（0-待判题，1-判题中，2-失败，3-成功）
     */
    private Integer status;

    /**
     * 判题信息（json对象）
     */
    private String judgeInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 5326271833389098016L;
}