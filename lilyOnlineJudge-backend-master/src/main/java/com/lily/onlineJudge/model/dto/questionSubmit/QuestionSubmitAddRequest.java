package com.lily.onlineJudge.model.dto.questionSubmit;

import com.lily.onlineJudge.model.entity.JudgeInfo;
import com.lily.onlineJudge.model.entity.User;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 代码提交
 *
* @author lily <a href="https://github.com/lilyWhenVia">作者github</a>
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 题目 id
     */
    @Min(0L)
    private Long questionId;

    /**
     * 使用的编程语言
     */
    @NotNull
    @NotEmpty
    @NotBlank
    private String language;

    /**
     * 提交代码
     */
    @NotNull
    @NotEmpty
    private String code;


    private static final long serialVersionUID = -4842608492107945195L;

}