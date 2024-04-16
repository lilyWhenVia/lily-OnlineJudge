package com.lily.nativecodesandbox.service;

import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lily via on 2024/4/14 22:22
 */
@Service
public interface CodeSandboxService {

    ExecuteCodeResponse doCompile(String codeFilePath);

    ExecuteCodeResponse doRun(List<String> inputList, String codeDirPath, Long TIME_OUT);
}
