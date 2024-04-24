package com.lily.nativecodesandbox.sandbox;

import com.lily.nativecodesandbox.model.ExecuteCodeRequest;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;

/**
 * Created by lily via on 2024/4/11 19:53
 */
public interface CodeSandbox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

}
