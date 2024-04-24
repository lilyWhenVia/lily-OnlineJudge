package com.lily.lilyojmodel.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 输入用例 与 标准输出答案
 * Created by lily via on 2024/4/8 0:11
 */
@Data
public class JudgeCase implements Serializable {

    private static final long serialVersionUID = 5539674954573652724L;

    /**
     * 单组用例输入
     */

    private String input;
    /**
     * 单组用例输出
     */
    private String output;
}
