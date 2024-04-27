package com.lily.nativecodesandbox.sandbox.JavaSandbox.impl;

import com.lily.nativecodesandbox.sandbox.CodeSandboxTemplate;
import com.lily.nativecodesandbox.common.ExecuteStatusEnum;
import com.lily.nativecodesandbox.model.CodeOutput;
import com.lily.nativecodesandbox.model.ExecuteCodeRequest;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import com.lily.nativecodesandbox.model.JudgeInfo;
import com.lily.nativecodesandbox.sandbox.CodeSandbox;
import com.lily.nativecodesandbox.sandbox.JavaSandbox.JavaCodeSandbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lily via on 2024/4/13 16:37
 */
@Slf4j
@Component
public class JavaCodeSandboxImpl extends CodeSandboxTemplate implements JavaCodeSandbox {

    public static void main(String[] args) {
        JavaCodeSandboxImpl javaCodeSandboxImpl = new JavaCodeSandboxImpl();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        // 去掉包名
        String code; // = ResourceUtil.readStr("D:\\JavaProject\\OnlineJudge\\NativeCodeSandbox\\src\\main\\java\\com\\lily\\nativecodesandbox\\Main.java", "GBK");
        code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(a + b);\n" +
                "        System.out.println(\"你好世界你好世界\");\n" +
                "    }\n" +
                "}";
        executeCodeRequest.setCode(code);

        List<String> inputList = List.of("1 2", "1 3");
        executeCodeRequest.setInputList(inputList);
        javaCodeSandboxImpl.executeCode(executeCodeRequest);
    }

    @Override
    public ExecuteCodeResponse doRun(List<String> inputList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        String inputArgs;
        long maxTime = 0L;
        final Boolean[] OUT_TIME_FLAG = {false};
        StopWatch runStopWatch = new StopWatch();
        List<CodeOutput> codeOutputList = new ArrayList<>();
        for (int i = 0; i < inputList.size(); i++) {
            inputArgs = inputList.get(i);
            String runCmd = String.format("java -Dfile.encoding=UTF-8 -cp %s Main %s",
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), "GBK"));
                    bufferedReader.lines().forEach(line -> outputMessage.append(line));
                    codeOutput.setInputExample(inputArgs);
                    codeOutput.setStdoutMessage(outputMessage.toString()); // 代码执行结果
                    codeOutputList.add(codeOutput);
                    log.info("执行成功");
                } else {
                    // 分批获取程序的异常输出
                    StringBuilder errorMessage = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream(), "GBK"));
                    bufferedReader.lines().forEach(line -> errorMessage.append(line).append("\n"));
                    codeOutput.setInputExample(inputArgs);
                    codeOutput.setStdErrorMessage(errorMessage.toString()); // 输出错误信息
                    codeOutputList.add(codeOutput);
                    executeCodeResponse.setCodeOutput(codeOutputList); // 便于最终统计size
                    // 是否为超时结束
                    if (inputList.size() != codeOutputList.size()) {
                        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_TIMEOUT.getExecuteStatus()); // 接口执行信息
                        executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_TIMEOUT.getStatusName());
                    } else {
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
        judgeInfo.setMessage(ExecuteStatusEnum.RUN_SUCCESS.getStatusName());
        executeCodeResponse.setJudgeInfo(judgeInfo);
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
