package com.lily.lilyojmodel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by lily via on 2024/4/8 21:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeInfo {
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
