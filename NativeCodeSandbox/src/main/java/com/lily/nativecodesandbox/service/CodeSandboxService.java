package com.lily.nativecodesandbox.service;

import com.lily.nativecodesandbox.dto.ExecuteCodeResponse;

import java.util.List;

/**
 * Created by lily via on 2024/4/14 22:22
 */
public interface CodeSandboxService {
    Boolean doCompile(String codeFilePath);

    ExecuteCodeResponse doRun(List<String> inputList, String codeDirPath, Long TIME_OUT);
}
