package com.lily.nativecodesandbox.sandbox.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.lily.nativecodesandbox.common.ExecuteStatusEnum;
import com.lily.nativecodesandbox.dto.CodeOutput;
import com.lily.nativecodesandbox.dto.ExecuteCodeRequest;
import com.lily.nativecodesandbox.dto.ExecuteCodeResponse;
import com.lily.nativecodesandbox.dto.JudgeInfo;
import com.lily.nativecodesandbox.sandbox.CodeSandbox;
import com.lily.nativecodesandbox.sandbox.ProcessUtils;
import com.lily.nativecodesandbox.service.CodeSandboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lily via on 2024/4/13 16:37
 */
@Slf4j
public class JavaCodeSandbox implements CodeSandbox {

    @Resource
    private ProcessUtils processUtils;

    @Resource
    private CodeSandboxService codeSandboxService;

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


    public static void main(String[] args) {
        JavaCodeSandbox javaCodeSandbox = new JavaCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();

        // 去掉包名
        String code = ResourceUtil.readStr("D:\\JavaProject\\OnlineJudge\\NativeCodeSandbox\\src\\main\\java\\com\\lily\\nativecodesandbox\\Main.java", StandardCharsets.UTF_8);
        code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(a + b);\n" +
                "\n" +
                "        try {\n" +
                "            Thread.sleep(1000*60);\n" +
                "        } catch (InterruptedException e) {\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "}\n";
        executeCodeRequest.setCode(code);

        List<String> inputList = List.of("1 2", "1 3");
        executeCodeRequest.setInputList(inputList);
        javaCodeSandbox.executeCode(executeCodeRequest);
    }


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String language = executeCodeRequest.getLanguage();
        String code = executeCodeRequest.getCode();

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
        System.out.println(userCodeFile.getAbsolutePath());

        // 2. 编译代码获取执行结果
        Boolean compileResult = codeSandboxService.doCompile(codeDirPath);
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();

        // 3. 执行程序
        ExecuteCodeResponse runCodeResponse = codeSandboxService.doRun(inputList, codeDirPath, TIME_OUT);

        List<CodeOutput> codeOutputList = runCodeResponse.getCodeOutput();
        String codeSandboxMes = runCodeResponse.getCodeSandboxMes();
        Integer codeSandboxStatus = runCodeResponse.getCodeSandboxStatus();

//        maxTime = runTimeCount.getTotalTimeMillis() / inputList.size();

        // 文件删除

        if (FileUtil.exist(codeDirPath)) {
            boolean del = FileUtil.del(codeDirPath);
            log.info("删除文件夹：" + codeDirPath + " " + (del ? "成功" : "失败"));
        }

        // 信息整理收集
        // 判断是否超时
        if (inputList.size() != codeOutputList.size()) {
            executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_TIMEOUT.getExecuteStatus());
        }
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(0L);
        judgeInfo.setMemory(256L);
        judgeInfo.setMessage("input full execute success");
        executeCodeResponse.setJudgeInfo(judgeInfo);
        executeCodeResponse.setCodeOutput(codeOutputList);
        System.out.printf(executeCodeResponse.toString());
        return executeCodeResponse;
    }

    /**
     * 程序执行异常处理
     */
    private ExecuteCodeResponse getErrorResponse(Exception e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setCodeSandboxMes(e.getMessage());
        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.SYSTEM_ERROR.getExecuteStatus());
        return executeCodeResponse;
    }

}
