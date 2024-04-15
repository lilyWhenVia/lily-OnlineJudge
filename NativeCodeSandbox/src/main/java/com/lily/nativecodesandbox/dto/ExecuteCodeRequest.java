package com.lily.nativecodesandbox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码沙箱运行接收参数类
 * Created by lily via on 2024/4/8 21:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecuteCodeRequest {

    /**
     * 输入用例列表
     */
    private List<String> inputList;


    /**
     * 使用的编程语言
     */
    private String language;

    /**
     * 提交代码
     */
    private String code;


}
