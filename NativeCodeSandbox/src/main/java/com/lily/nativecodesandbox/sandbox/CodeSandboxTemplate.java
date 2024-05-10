package com.lily.nativecodesandbox.sandbox;

import cn.hutool.core.io.FileUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.lily.nativecodesandbox.common.ExecuteStatusEnum;
import com.lily.nativecodesandbox.model.ExecuteCodeRequest;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import com.lily.nativecodesandbox.model.JudgeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

/**
 * Created by lily via on 2024/4/18 22:36
 */
@Slf4j
@Component
public abstract class CodeSandboxTemplate implements CodeSandbox {


    //------------------------全局配置------------------------

    /**
     * 全局存放代码代码目录名
     */
    public final String GLOBAL_CODE_DIR_NAME = "execCode";

    /**
     * 用户当前工作的目录
     */
    public final String userDir = System.getProperty("user.dir");

    /**
     * 全局代码目录路径
     */
    public final String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

    public final String codeUUID = UUID.randomUUID().toString();
    /**
     * 隔离用户代码文件夹目录路径
     */
    public final String codeDirPath = globalCodePathName + File.separator + codeUUID;

    /**
     * 用户代码文件名
     */
    public final String codeFileName = "Main";

    /**
     * 用户代码文件路径
     */
    public final String codeFilePath = codeDirPath + File.separator + codeFileName;

    /**
     * 执行最大时间
     */
    public final Long TIME_OUT = 1000L;


    public String SECURITY_MANAGER_PATH = userDir + File.separator + "src" + File.separator + "main" +
            File.separator + "java" + File.separator + "com" + File.separator + "lily" + File.separator + "nativecodesandbox" + File.separator + "security";
    public String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager.java";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        // 1. 把用户的代码保存为文件
        File userCodeFile = getUserFile(code);
        // 2. 编译代码获取执行结果
        ExecuteCodeResponse executeCodeResponse;
        executeCodeResponse = doCompile(userCodeFile.getAbsolutePath());
        Integer compileStatus = executeCodeResponse.getCodeSandboxStatus();
        // 3. 编译成功执行代码
        if (compileStatus == ExecuteStatusEnum.COMPILE_SUCCESS.getExecuteStatus()) {
            // 3. 编译成功即执行程序
            executeCodeResponse = doRun(inputList);
        }
        log.info("返回执行结果" + executeCodeResponse);
        // 4. 删除用户代码文件
        Boolean b = delUserCodeFile();
        log.info("删除文件夹：" + codeDirPath + " " + (b ? "成功" : "失败"));
        return executeCodeResponse;
    }

    /**
     * 得到用户代码文件
     *
     * @param code
     * @return
     */
    public File getUserFile(String code) {
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
        // 把用户的代码隔离存放
        if (!FileUtil.exist(codeDirPath)) {
            FileUtil.mkdir(codeDirPath);
        }
        // 新建用户代码文件
        File userCodeFile = FileUtil.writeString(code, codeFilePath, "UTF-8");
        return userCodeFile;
    }

    /**
     * 编译
     *
     * @param codeFilePath
     * @return
     */
    public ExecuteCodeResponse doCompile(String codeFilePath) {
//        2. 编译代码获取执行结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        JudgeInfo judgeInfo = new JudgeInfo();
        String compileCmd = String.format("javac -encoding UTF-8 %s", codeFilePath);
        int exitValue;
        try {
            Process process = Runtime.getRuntime().exec(compileCmd);
            // 等待程序执行并获取信息
            //  程序退出信息
            exitValue = process.waitFor();
            if (exitValue == 0) {
                StringBuilder outputMessage = new StringBuilder();
                // todo 验证输出流
                // 分批获取程序的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
                bufferedReader.lines().forEach(line -> outputMessage.append(line).append("\n"));
                executeCodeResponse.setCodeSandboxMes(outputMessage.toString()); // 编译信息
                executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.COMPILE_SUCCESS.getExecuteStatus()); // 编译成功状态
                judgeInfo.setMessage(ExecuteStatusEnum.COMPILE_SUCCESS.getStatusName());
                log.info("编译成功");
            } else {
                // 分批获取程序的异常输出
                StringBuilder errorMessage = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
                bufferedReader.lines().forEach(line -> errorMessage.append(line).append("\n"));
                executeCodeResponse.setCodeSandboxMes(errorMessage.toString()); // 编译信息
                executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.COMPILE_FAIL.getExecuteStatus()); // 编译失败
                judgeInfo.setMessage(ExecuteStatusEnum.COMPILE_FAIL.getStatusName());
                log.error("编译失败");
            }
        } catch (IOException | InterruptedException e) {
            return getErrorResponse(e);
        }
        // 编译成功状态
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }


    /**
     * 执行编译文件
     *
     * @param inputList
     * @return
     */
    public ExecuteCodeResponse doRun(List<String> inputList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
//        String inputArgs;
//        long maxTime = 0L;
//        final Boolean[] OUT_TIME_FLAG = {false};
//        StopWatch runStopWatch = new StopWatch();
//        List<CodeOutput> codeOutputList = new ArrayList<>();
//        for (int i = 0; i < inputList.size(); i++) {
//            inputArgs = inputList.get(i);
//            String runCmd = String.format("java -Dfile.encoding=UTF-8 -cp %s Main %s",
//                    codeDirPath, inputArgs);
//            try {
//                // 执行程序
//                runStopWatch.start();
//                Process runProcess = Runtime.getRuntime().exec(runCmd);
//                // 启动守护线程
//                Boolean isTimeOut = this.timeControlThread(runProcess, TIME_OUT);
//                // 等待程序执行并获取信息
//                CodeOutput codeOutput = new CodeOutput();
//                //  程序退出信息
//                int exitValue = runProcess.waitFor();
//                // waitFor方法会导致当前线程等待
//                runStopWatch.stop();
//                maxTime = Math.max(maxTime, runStopWatch.getLastTaskTimeMillis());
//                /**
//                 * 判断结果并统计
//                 */
//                if (exitValue == 0) {
//                    StringBuilder outputMessage = new StringBuilder();
//                    // todo 验证输出流
//                    // 分批获取程序的正常输出
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), "GBK"));
//                    bufferedReader.lines().forEach(line -> outputMessage.append(line));
//                    codeOutput.setInputExample(inputArgs);
//                    codeOutput.setStdoutMessage(outputMessage.toString()); // 代码执行结果
//                    codeOutputList.add(codeOutput);
//                    log.info("执行成功");
//                } else {
//                    // 分批获取程序的异常输出
//                    StringBuilder errorMessage = new StringBuilder();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream(), "GBK"));
//                    bufferedReader.lines().forEach(line -> errorMessage.append(line).append("\n"));
//                    codeOutput.setInputExample(inputArgs);
//                    codeOutput.setStdErrorMessage(errorMessage.toString()); // 输出错误信息
//                    codeOutputList.add(codeOutput);
//                    executeCodeResponse.setCodeOutput(codeOutputList); // 便于最终统计size
//                    // 是否为超时结束
//                    if (inputList.size() != codeOutputList.size()) {
//                        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_TIMEOUT.getExecuteStatus()); // 接口执行信息
//                        executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_TIMEOUT.getStatusName());
//                    } else {
//                        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_FAIL.getExecuteStatus()); // 执行失败枚举
//                        executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_FAIL.getStatusName());
//                    }
//                    log.error("执行失败");
//                    // 只要有一个用例失败即结束执行,并统计返回结果
//                    return executeCodeResponse;
//                }
//            } catch (IOException | InterruptedException e) {
//                return getErrorResponse(e);
//            }
//        }
////            所有用例成功执行，统计返回结果
//        executeCodeResponse.setCodeOutput(codeOutputList);
//        executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_SUCCESS.getStatusName());
//        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_SUCCESS.getExecuteStatus()); // 执行成功状态
//        JudgeInfo judgeInfo = new JudgeInfo();
//        // 统计
//        judgeInfo.setTime(maxTime);
//        judgeInfo.setMemory(256L);
//        judgeInfo.setStack(0L);
//        judgeInfo.setMessage("execute success");
//        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }


    /**
     * 删除文件
     *
     * @return
     */
    public Boolean delUserCodeFile() {
        // 文件删除
        if (FileUtil.exist(codeDirPath)) {
            return FileUtil.del(codeDirPath);
        }
        return true;
    }

    /**
     * 系统异常处理
     */
    public ExecuteCodeResponse getErrorResponse(Exception e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setCodeSandboxMes(e.getMessage());
        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.SYSTEM_ERROR.getExecuteStatus());
        return executeCodeResponse;
    }
}
