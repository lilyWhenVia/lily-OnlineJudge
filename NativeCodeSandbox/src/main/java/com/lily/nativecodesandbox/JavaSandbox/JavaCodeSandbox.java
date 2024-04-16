package com.lily.nativecodesandbox.JavaSandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.lily.nativecodesandbox.common.ExecuteStatusEnum;
import com.lily.nativecodesandbox.model.ExecuteCodeRequest;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import com.lily.nativecodesandbox.sandbox.CodeSandbox;
import com.lily.nativecodesandbox.service.CodeSandboxService;
import com.lily.nativecodesandbox.service.impl.CodeSandboxServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Created by lily via on 2024/4/13 16:37
 */
@Slf4j
public class JavaCodeSandbox implements CodeSandbox {


//    @Resource
    private static CodeSandboxService codeSandboxService;

    /**
     * 全局存放代码代码目录名
     */
    private final String GLOBAL_CODE_DIR_NAME = "execCode";

    /**
     * 用户当前工作的目录
     */
    private final String userDir = System.getProperty("user.dir");

    /**
     * 全局代码目录路径
     */
    private final String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

    /**
     * 隔离用户代码文件夹目录路径
     */
    private final String codeDirPath = globalCodePathName + File.separator + UUID.randomUUID();

    /**
     * 用户代码文件名
     */
    private final String codeFileName = "Main.java";

    /**
     * 用户代码文件路径
     */
    private final String codeFilePath = codeDirPath + File.separator + codeFileName;

    /**
     * 执行最大时间
     */
    private final Long TIME_OUT = 1000L;


    String SECURITY_MANAGER_PATH = "";
    String SECURITY_MANAGER_CLASS_NAME = "";


    public static void main(String[] args) {
        JavaCodeSandbox javaCodeSandbox = new JavaCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();

        codeSandboxService = new CodeSandboxServiceImpl();
        // 去掉包名
        String code = ResourceUtil.readStr("D:\\JavaProject\\OnlineJudge\\NativeCodeSandbox\\src\\main\\java\\com\\lily\\nativecodesandbox\\Main.java", StandardCharsets.UTF_8);
        code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(a + b);\n" +
                "    }\n" +
                "}";
        executeCodeRequest.setCode(code);

        List<String> inputList = List.of("1 2", "1 3");
        executeCodeRequest.setInputList(inputList);
        javaCodeSandbox.executeCode(executeCodeRequest);
    }


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        // 设置编码
        System.setProperty("console.encoding", "UTF-8");
        // 1. 把用户的代码保存为文件
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
        // 把用户的代码隔离存放
        if (!FileUtil.exist(codeDirPath)) {
            FileUtil.mkdir(codeDirPath);
        }
        // 新建用户代码文件
        File userCodeFile = FileUtil.writeString(code, codeFilePath, StandardCharsets.UTF_8);
        /**
         * 编译运行
         */
        // 2. 编译代码获取执行结果
        ExecuteCodeResponse executeCodeResponse;
        executeCodeResponse = codeSandboxService.doCompile( userCodeFile.getAbsolutePath());
        Integer compileStatus = executeCodeResponse.getCodeSandboxStatus();
        if (compileStatus == ExecuteStatusEnum.COMPILE_SUCCESS.getExecuteStatus()) {
            // 3. 编译成功即执行程序
            executeCodeResponse = codeSandboxService.doRun(inputList, codeDirPath, TIME_OUT);
        }

//        // 文件删除
        if (FileUtil.exist(codeDirPath)) {
            boolean del = FileUtil.del(codeDirPath);
            log.info("删除文件夹：" + codeDirPath + " " + (del ? "成功" : "失败"));
        }
        System.out.println(executeCodeResponse);
        return executeCodeResponse;
    }


}
