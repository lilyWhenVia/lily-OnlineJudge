package com.lily.nativecodesandbox.sandbox.dockerSandbox.impl;

import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.lily.nativecodesandbox.Once.FirstInitJob;
import com.lily.nativecodesandbox.sandbox.CodeSandboxTemplate;
import com.lily.nativecodesandbox.common.ExecuteStatusEnum;
import com.lily.nativecodesandbox.model.CodeOutput;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import com.lily.nativecodesandbox.model.JudgeInfo;
import com.lily.nativecodesandbox.sandbox.CodeSandbox;
import com.lily.nativecodesandbox.sandbox.dockerSandbox.DockerCodeSandbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.lily.nativecodesandbox.Once.FirstInitJob.containerId;

/**
 * Created by lily via on 2024/4/16 11:17
 */
@Slf4j
@Component
public class DockerSandboxImpl extends CodeSandboxTemplate implements DockerCodeSandbox {

    // ------------------------docker配置------------------------
    @Value("${Docker.host}")
    public static String host = "192.168.70.130";

    @Value("${Docker.host:2375}")
    public static String port = "2375";

    @Value("${Docker.Bind.path}")
    public static String bindPath = File.separator + "home" + File.separator + "lily" + File.separator + "oj-codeSandbox";

    @Value("${Docker.Bind.volume}")
    public static String volume = File.separator + "app";

    /**
     * docker内挂载路径目录
     * /app/execCode/uuid
     */
    private final String codeDirPath = volume+File.separator+GLOBAL_CODE_DIR_NAME+File.separator+ codeUUID;

    // 初始化docker
    static {
        containerId= FirstInitJob.InitContainer();
    }

    @Override
    public ExecuteCodeResponse doCompile(String codeFilePath) {
//        2. 编译代码获取执行结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        JudgeInfo judgeInfo = new JudgeInfo();
        // docker exec 5af9c3b31b87 javac /app/execCode/uuid/Main.java
        String[] compileCmd = {"javac", "-encoding", "UTF-8", codeDirPath+File.separator+codeFileName+".java"};
        DockerClient dockerClient = getDockerClient();
        // 创建编译指令
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(compileCmd)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withAttachStdin(true)
                .exec();

        String execId = execCreateCmdResponse.getId();
        // 定义执行结果变量
        String[] stdErrorMessage = new String[1];
        String[] stdoutMessage = new String[1];
        boolean[] compileFail={false};

        /**
         * 执行结果回调函数定义
         */
        ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback(System.out, System.err) {
            @Override
            public void onComplete() {
                System.out.println("编译结束");
                super.onComplete();
            }

            @Override
            public void onNext(Frame frame) {
                StreamType streamType = frame.getStreamType();
                String terminalOutput = new String(frame.getPayload(), StandardCharsets.UTF_8);
                if (StreamType.STDERR.equals(streamType)) {
                    compileFail[0] = true;
                    stdErrorMessage[0] = terminalOutput;
                    log.info("compile success: "+terminalOutput);
                } else {
                    stdoutMessage[0] = terminalOutput;
                    log.error("compile filed: "+terminalOutput);
                }
                System.out.println("输出结果：" + terminalOutput);
                super.onNext(frame);
            }
        };

        /**
         * 程序开始执行
         */
        try {
            dockerClient.execStartCmd(execId)
                    .exec(execStartResultCallback)
                    .awaitCompletion();
        } catch (InterruptedException e) {
            return getErrorResponse(e);
        }

        // todo 收集结果
        if (compileFail[0]){
            executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.COMPILE_FAIL.getStatusName());
            executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.COMPILE_FAIL.getExecuteStatus());
            judgeInfo.setMessage(ExecuteStatusEnum.COMPILE_FAIL.getStatusName());
        }else{
            executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.COMPILE_SUCCESS.getStatusName());
            executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.COMPILE_SUCCESS.getExecuteStatus()); // 执行成功状态
            judgeInfo.setMessage(ExecuteStatusEnum.COMPILE_SUCCESS.getStatusName());
        }
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
    @Override
    public ExecuteCodeResponse doRun(List<String> inputList){

        DockerClient dockerClient = getDockerClient();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        JudgeInfo judgeInfo = new JudgeInfo();
        List<CodeOutput> codeOutputList = new ArrayList<>();

        long maxTime = 0L;
        // 计时器
        StopWatch stopWatch = new StopWatch();

        // 检测是否有过执行错误
        final boolean[] runFail = {false};
        final boolean[] timeout = {true};
        final Long[] maxMemory = new Long[1];
        final Long[] maxStack = new Long[1];
        maxMemory[0] = 0L; // 初始化
        maxStack[0] = 0L; // 初始化

        /**
         * 内存检测
         */
        StatsCmd statsCmd = dockerClient.statsCmd(containerId);
        ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
            @Override
            public void onNext(Statistics statistics) {
                long currentMemory = (Long) statistics.getMemoryStats().getUsage();
                long currentStack = (Long) statistics.getMemoryStats().getUsage();
                maxMemory[0] = Math.max(currentMemory, maxMemory[0]);
                maxStack[0] = Math.max(currentStack, maxStack[0]);
                System.out.println("内存占用：" + maxMemory[0]);
            }

            @Override
            public void close() throws IOException {

            }

            @Override
            public void onStart(Closeable closeable) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }

        });
        // 内存检测一直继续，每隔一段时间检测一下内存，不会结束
        statsCmd.exec(statisticsResultCallback);

        // docker exec 5af9c3b31b87 java -cp /app/execCode Main 2 3
        String[] runRootCmd = {"java", "-Dfile.encoding=UTF-8", "-cp", codeDirPath, codeFileName};
        /**
         * 循环执行用例开始
         */
        for (int i = 0; i < inputList.size(); i++) {

            String inputArgs = inputList.get(i);
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(runRootCmd, inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdout(true)
                    .withAttachStdin(true)
                    .exec();

            String execId = execCreateCmdResponse.getId();

            // 执行结果变量初始化
            CodeOutput codeOutput = new CodeOutput();
            final String[] stdoutMessage = new String[1];
            final String[] stdErrorMessage = new String[1];


            /**
             * 执行结果回调函数定义
             */
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback(System.out, System.err) {
                @Override
                public void onComplete() {
                    // 如果执行完成，则表示没超时
                    timeout[0] = false;
                    System.out.println("执行用例结束");
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    String terminalOutput = new String(frame.getPayload(), StandardCharsets.UTF_8);
                    if (StreamType.STDERR.equals(streamType)) {
                        runFail[0] = true;
                        stdErrorMessage[0] = terminalOutput;
                    } else {
                        stdoutMessage[0] = terminalOutput;
                    }
                    log.info("执行结果：" + terminalOutput);
                    super.onNext(frame);
                }
            };

            /**
             * 程序开始执行
             */
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MINUTES);
                stopWatch.stop();

            } catch (InterruptedException e) {
                return getErrorResponse(e);
            }

            maxTime = Math.max(maxTime, stopWatch.getLastTaskTimeMillis());
            codeOutput.setStdoutMessage(stdoutMessage[0]);
            codeOutput.setStdErrorMessage(stdErrorMessage[0]);
            codeOutput.setInputExample(inputArgs);
            codeOutputList.add(codeOutput);
        }

        // 程序内存监控关闭
        statsCmd.close();

        // todo 收集结果
        if (timeout[0]) {
            executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_TIMEOUT.getStatusName());
            executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_TIMEOUT.getExecuteStatus());
            judgeInfo.setMessage(ExecuteStatusEnum.RUN_TIMEOUT.getStatusName());

        } else if (runFail[0]){
            executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_FAIL.getStatusName());
            executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_FAIL.getExecuteStatus());
            judgeInfo.setMessage(ExecuteStatusEnum.RUN_FAIL.getStatusName());
        }else{
            executeCodeResponse.setCodeSandboxMes(ExecuteStatusEnum.RUN_SUCCESS.getStatusName());
            executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.RUN_SUCCESS.getExecuteStatus()); // 执行成功状态
            judgeInfo.setMessage(ExecuteStatusEnum.RUN_SUCCESS.getStatusName());
        }
        executeCodeResponse.setCodeOutput(codeOutputList);
        // 统计
        judgeInfo.setTime(maxTime);
        judgeInfo.setMemory(maxMemory[0]);
        judgeInfo.setStack(maxStack[0]);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    private DockerClient getDockerClient(){
        // 连接docker服务器 获得dockerClient
        DockerClient dockerClient = DockerClientBuilder
                .getInstance("tcp://" + host + ":" + port).build();
        return dockerClient;
    }
}
