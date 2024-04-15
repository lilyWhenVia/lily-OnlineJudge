package com.lily.nativecodesandbox.service.impl;

import cn.hutool.core.date.DateTime;
import com.lily.nativecodesandbox.common.ExecuteStatusEnum;
import com.lily.nativecodesandbox.dto.CodeOutput;
import com.lily.nativecodesandbox.dto.ExecuteCodeResponse;
import com.lily.nativecodesandbox.service.CodeSandboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lily via on 2024/4/14 22:22
 */
@Slf4j
public class CodeSandboxServiceImpl implements CodeSandboxService {

    public void getProcessByCmd() {
        // TODO
        ProcessBuilder pb =   new ProcessBuilder("java", "-Xmx256m", "-Dfile.encoding=UTF-8");
        Map<String, String> env = pb.environment();
        env. put("VAR1", "myValue");
        env. remove("OTHERVAR");
        env. put("VAR2", env. get("VAR1") + "suffix");
        pb. directory(new File("myDir"));
        String userLogFile = "userDir" + File.separator + "log";
        File codeLog = new File(userLogFile); pb. redirectErrorStream(true);
        pb. redirectOutput(ProcessBuilder.Redirect.appendTo(codeLog));
        try {
            Process p = pb. start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean doCompile(String codeFilePath) {
        // TODO
//        2. 编译代码获取执行结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        String compileCmd = String.format("javac -encoding utf-8 %s", codeFilePath);
        try {

            Process process = Runtime.getRuntime().exec(compileCmd);
            // 等待程序执行并获取信息
            //  程序退出信息
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                StringBuilder outputMessage = new StringBuilder();
                // todo 验证输出流
                // 分批获取程序的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                bufferedReader.lines().forEach(line -> outputMessage.append(line).append("\n"));
                executeCodeResponse.setCodeSandboxMes(outputMessage.toString()); // 编译信息
                executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.COMPILE_SUCCESS.getExecuteStatus()); // 编译成功状态
                System.out.println("编译成功");
            } else {
                // 分批获取程序的异常输出
                StringBuilder errorMessage = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                bufferedReader.lines().forEach(line -> errorMessage.append(line).append("\n"));
                executeCodeResponse.setCodeSandboxMes(errorMessage.toString()); // 编译信息
                executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.COMPILE_FAIL.getExecuteStatus()); // 编译失败
                System.out.println("编译失败");
                // todo 删除文件
//                return executeCodeResponse;
            }
        } catch (IOException | InterruptedException e) {
            return null;
        }

        return false;
    }


    @Override
    public ExecuteCodeResponse doRun(List<String> inputList, String codeDirPath, Long TIME_OUT) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        String SECURITY_MANAGER_PATH = "";
        String SECURITY_MANAGER_CLASS_NAME = "";
        String inputArgs = "";
        long maxTime = 0L;
        List<CodeOutput> codeOutputList = new ArrayList<>();
        for (int i = 0; i < inputList.size(); i++) {
            inputArgs = inputList.get(i);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s",
                    codeDirPath, inputArgs);
            final long[] time = {0};
            try {
                // 执行程序
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制，添加守护进程
                String finalInputArgs = inputArgs;
                int finalI = i;
                Thread daemonThread = new Thread(() -> {
                    System.out.printf("守护线程启动");
                    StopWatch runTimeCount = new StopWatch();
                    runTimeCount.start();
                    while (true) {
//                        System.out.printf("runProcess is alive");
                        // 超时控制
                        System.out.printf("总时间：%d\n", runTimeCount.getTotalTimeMillis());
                        if (runProcess.isAlive() && runTimeCount.getTotalTimeMillis() > TIME_OUT) {
                            runProcess.destroyForcibly();
                            log.error("运行超时：" + this.getClass() + " 于 " + DateTime.now() + "在执行第" + finalI + " 个 " + finalInputArgs + "用例时");
                            break;
                        }
                        // 间隔TIMEOUT时间检测Process线程是否存活

                    }
                    // process进程正常死亡
                    runTimeCount.stop();
                    time[0] = runTimeCount.getLastTaskTimeMillis();
                });
                daemonThread.setDaemon(true);
                daemonThread.start();
                // 等待程序执行并获取信息
                CodeOutput codeOutput = new CodeOutput();
                //  程序退出信息
                // waitFor方法会导致当前线程等待
                int exitValue = runProcess.waitFor();
                if (exitValue == 0) {
                    StringBuilder outputMessage = new StringBuilder();
                    // todo 验证输出流
                    // 分批获取程序的正常输出
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                    bufferedReader.lines().forEach(line -> outputMessage.append(line).append("\n"));

                    codeOutput.setCodeExecute(outputMessage.toString()); // 执行信息
                    codeOutput.setStdoutMessage(inputArgs); //  输入信息
                    executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_SUCCESS.getExecuteStatus()); // 执行成功状态
                    codeOutputList.add(codeOutput);
                    System.out.println("执行成功");
                } else {
                    // 分批获取程序的异常输出
                    StringBuilder errorMessage = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                    bufferedReader.lines().forEach(line -> errorMessage.append(line).append("\n"));
                    codeOutput.setStdErrorMessage(inputArgs); // 输入信息
                    executeCodeResponse.setCodeSandboxMes(errorMessage.toString()); // 接口执行信息
                    executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_FAIL.getExecuteStatus()); // 执行失败枚举
                    codeOutputList.add(codeOutput);
                    System.out.println("执行失败");
                }
            } catch (IOException | InterruptedException e) {
                return null;
            }
            // 统计最大时间
//            System.out.printf("总时间：%d, 最后一个任务时间：%d\n", runTimeCount.getTotalTimeMillis(), runTimeCount.getLastTaskTimeMillis());
            maxTime = Math.max(maxTime, time[0]);
        }
        return null;
    }

}
