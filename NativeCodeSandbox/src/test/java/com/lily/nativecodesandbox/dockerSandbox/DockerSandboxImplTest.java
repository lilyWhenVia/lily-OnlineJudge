package com.lily.nativecodesandbox.dockerSandbox;

import com.lily.nativecodesandbox.model.ExecuteCodeRequest;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import com.lily.nativecodesandbox.sandbox.dockerSandbox.impl.DockerSandboxImpl;

import java.util.List;

/**
 * Created by lily via on 2024/4/24 12:06
 */
class DockerSandboxImplTest {

    public void testExecuteCode() {
        /**
         * 执行力一段示例代码
         */
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(a + b);\n" +
                "System.out.println(\"hello world 你好世界\");\n" +
                "    }\n" +
                "}";
        executeCodeRequest.setCode(code);
        List<String> inputList = List.of("1 2", "1 3");
        executeCodeRequest.setInputList(inputList);
        DockerSandboxImpl dockerSandbox = new DockerSandboxImpl();
        ExecuteCodeResponse executeCodeResponse;
//      executeCodeResponse = dockerSandbox.executeCode(executeCodeRequest);
        executeCodeResponse = dockerSandbox.doCompile(dockerSandbox.globalCodePathName);
        executeCodeResponse = dockerSandbox.doRun(inputList);
        System.out.println(executeCodeResponse);
    }
}