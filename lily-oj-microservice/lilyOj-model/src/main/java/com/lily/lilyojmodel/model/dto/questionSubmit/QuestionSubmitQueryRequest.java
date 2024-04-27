package com.lily.lilyojmodel.model.dto.questionSubmit;

import com.lily.lilyojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询请求
 *
* @author lily <a href="https://github.com/lilyWhenVia">come to find lily</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

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
     * 提交状态（0-待判题，1-判题中，2-失败，3-成功）
     */
    private Integer status;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = -4609897844069626636L;
}