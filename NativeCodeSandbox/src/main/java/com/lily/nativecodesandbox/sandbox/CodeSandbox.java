package com.lily.nativecodesandbox.sandbox;

import com.lily.nativecodesandbox.dto.ExecuteCodeRequest;
import com.lily.nativecodesandbox.dto.ExecuteCodeResponse;

/**
 * Created by lily via on 2024/4/11 19:53
 */
public interface CodeSandbox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);


}
