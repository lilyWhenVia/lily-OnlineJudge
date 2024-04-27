package com.lily.lilyojmodel.model.dto.judge;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * 题目本身的限制属性
 * Created by lily via on 2024/4/7 23:58
 */
@Data
public class JudgeConfig implements Serializable {

    private static final long serialVersionUID = -912509403172952943L;

    /**
     * 时间限制
     */
    private Long timeLimit;

    /**
     * 内存限制
     */
    private Long memoryLimit;


    /**
     * 栈大小限制
     */
    private Long stackLimit;


}
