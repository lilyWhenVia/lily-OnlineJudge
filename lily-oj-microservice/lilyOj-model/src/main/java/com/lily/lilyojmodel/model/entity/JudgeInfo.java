package com.lily.lilyojmodel.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回给前端的执行结果信息
 * Created by lily via on 2024/4/7 23:57
 */
@Data
public class JudgeInfo implements Serializable {

    private static final long serialVersionUID = -2797891146285666133L;

    /**
     * 题目 执行信息
     */
    private String message;

    /**
     * 题目 执行时间
     * 单位ms
     */
    private Long time;

    /**
     * 题目 执行内存消耗
     */
    private Long memory;

    /**
     * 调用栈大小
     */
    private Long stack;

}
