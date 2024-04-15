package com.lily.nativecodesandbox.service.impl;

import com.lily.nativecodesandbox.common.ExecuteStatusEnum;
import com.lily.nativecodesandbox.dto.CodeOutput;
import com.lily.nativecodesandbox.dto.ExecuteCodeResponse;
import com.lily.nativecodesandbox.dto.JudgeInfo;
import com.lily.nativecodesandbox.service.CodeSandboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lily via on 2024/4/14 22:22
 */
@Service
@Slf4j
public class CodeSandboxServiceImpl implements CodeSandboxService {

    public void getProcessByCmd(String logFilePath) {
        // TODO
        ProcessBuilder pb = new ProcessBuilder("javac -encoding gbk", "codeFilePath");
        Map<String, String> env = pb.environment();
        env.put("VAR1", "myValue");
        pb.directory(new File("myDir"));
        String userLogFile = logFilePath + File.separator + "log";
        File codeLog = new File(userLogFile);
        pb.redirectErrorStream(true);
        pb.redirectError(ProcessBuilder.Redirect.appendTo(codeLog));
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(codeLog));
        try {
            Process p = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExecuteCodeResponse doCompile(String codeFilePath) {
        // TODO
//        2. 编译代码获取执行结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        String compileCmd = String.format("javac -encoding GBK %s", codeFilePath);
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
                log.info("编译成功");
            } else {
                // 分批获取程序的异常输出
                StringBuilder errorMessage = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
                bufferedReader.lines().forEach(line -> errorMessage.append(line).append("\n"));
//                String readLine;
//                while ((readLine = bufferedReader.readLine()) != null){
//                    errorMessage.append(readLine).append("\n");
//                }
                executeCodeResponse.setCodeSandboxMes(errorMessage.toString()); // 编译信息
                executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.COMPILE_FAIL.getExecuteStatus()); // 编译失败
                log.error("编译失败");
            }
        } catch (IOException | InterruptedException e) {
            return getErrorResponse(e);
        }
        return executeCodeResponse;
    }


    @Override
    public ExecuteCodeResponse doRun(List<String> inputList, String codeDirPath, Long TIME_OUT) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        String inputArgs;
        long maxTime = 0L;
        final Boolean[] OUT_TIME_FLAG = {false};
        StopWatch runStopWatch = new StopWatch();
        List<CodeOutput> codeOutputList = new ArrayList<>();
        for (int i = 0; i < inputList.size(); i++) {
            inputArgs = inputList.get(i);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=gbk -cp %s Main %s",
                    codeDirPath, inputArgs);
            try {
                // 执行程序
                runStopWatch.start();
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 启动守护线程
                Boolean isTimeOut = this.timeControlThread(runProcess, TIME_OUT);
                // 等待程序执行并获取信息
                CodeOutput codeOutput = new CodeOutput();
                //  程序退出信息
                int exitValue = runProcess.waitFor();
                // waitFor方法会导致当前线程等待
                runStopWatch.stop();
                maxTime = Math.max(maxTime, runStopWatch.getLastTaskTimeMillis());
                /**
                 * 判断结果并统计
                 */
                if (exitValue == 0) {
                    StringBuilder outputMessage = new StringBuilder();
                    // todo 验证输出流
                    // 分批获取程序的正常输出
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(),"GBK"));
                    bufferedReader.lines().forEach(line -> outputMessage.append(line).append("\n"));
                    codeOutput.setInputExample(inputArgs);
                    codeOutput.setStdoutMessage(outputMessage.toString()); // 代码执行结果
                    codeOutputList.add(codeOutput);
                    log.info("执行成功");
                } else {
                    // 分批获取程序的异常输出
                    StringBuilder errorMessage = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream(), "GBK"));
                    bufferedReader.lines().forEach(line -> errorMessage.append(line).append("\n"));
                    // todo 返回参数设计
                    codeOutput.setInputExample(inputArgs);
                    codeOutput.setStdErrorMessage(errorMessage.toString()); // 输出错误信息
                    codeOutputList.add(codeOutput);
                    executeCodeResponse.setCodeOutput(codeOutputList); // 便于最终统计size
                    // 是否为超时结束
                    if (inputList.size() != codeOutputList.size()){
                        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_TIMEOUT.getExecuteStatus()); // 接口执行信息
                        executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_TIMEOUT.getStatusName());
                    }else {
                        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_FAIL.getExecuteStatus()); // 执行失败枚举
                        executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_FAIL.getStatusName());
                    }
                    log.error("执行失败");
                    // 只要有一个用例失败即结束执行,并统计返回结果
                    return executeCodeResponse;
                }
            } catch (IOException | InterruptedException e) {
                return getErrorResponse(e);
            }
//            所有用例成功执行，统计返回结果
            executeCodeResponse.setCodeOutput(codeOutputList);
            executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_SUCCESS.getStatusName());
            executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_SUCCESS.getExecuteStatus()); // 执行成功状态
            JudgeInfo judgeInfo = new JudgeInfo();
            // 统计
            judgeInfo.setTime(maxTime);
            judgeInfo.setMemory(256L);
            judgeInfo.setStack(0L);
            judgeInfo.setMessage("execute success");
            executeCodeResponse.setJudgeInfo(judgeInfo);
        }
        return executeCodeResponse;
    }


    /**
     * 系统异常处理
     */
    private ExecuteCodeResponse getErrorResponse(Exception e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setCodeSandboxMes(e.getMessage());
        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.SYSTEM_ERROR.getExecuteStatus());
        return executeCodeResponse;
    }

    /**
     * 查看程序进程是否执行超时
     *
     * @param runProcess 代码执行进程
     * @param TIME_OUT   最大允许运行时间
     * @return 超时返回true，未超时正常结束返回false
     */
    private Boolean timeControlThread(Process runProcess, Long TIME_OUT) {
        AtomicBoolean out_time_flag = new AtomicBoolean(false);
        // 超时控制，添加守护进程
        Thread daemonThread = new Thread(() -> {
            System.out.println("守护线程启动");
            try {
                Thread.sleep(TIME_OUT);
                if (runProcess.isAlive()) {
                    log.error("执行超时");
                    out_time_flag.set(true);
                    runProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        daemonThread.setDaemon(true);
        daemonThread.start();
        return out_time_flag.get();
    }

}
