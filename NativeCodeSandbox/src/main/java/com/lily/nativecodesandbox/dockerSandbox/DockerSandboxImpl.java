package com.lily.nativecodesandbox.dockerSandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.lily.nativecodesandbox.common.ExecuteStatusEnum;
import com.lily.nativecodesandbox.model.CodeOutput;
import com.lily.nativecodesandbox.model.ExecuteCodeRequest;
import com.lily.nativecodesandbox.model.ExecuteCodeResponse;
import com.lily.nativecodesandbox.model.JudgeInfo;
import com.lily.nativecodesandbox.sandbox.CodeSandbox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by lily via on 2024/4/16 11:17
 */
@Slf4j
public class DockerSandboxImpl implements CodeSandbox {

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
    private static final String path = System.getProperty("user.dir") + File.separator + "execCode";

    /**
     * 用户代码文件名
     */
    private final String codeFileName = "Main.java";

    private final String volumePath = "/app";
    private final String volumeCodePath = volumePath + File.separator + GLOBAL_CODE_DIR_NAME;


    /**
     * 用户代码文件路径
     */
    private final String codeFilePath = codeDirPath + File.separator + codeFileName;

    /**
     * 执行最大时间
     */
    private final Long TIME_OUT = 1000L;


    public static void main(String[] args) {
        // 连接docker服务器
        DockerClient dockerClient = DockerClientBuilder
                .getInstance("tcp://192.168.70.130:2375").build();
//        // 2. 拉取镜像
        String imageName = "openjdk:8-alpine";
        PullImageResultCallback imageResultCallback = dockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback());
        imageResultCallback.awaitSuccess();
        // 2. 查看镜像
        dockerClient.listImagesCmd().exec().forEach(image -> {
            System.out.println("查看镜像: " + image.getRepoTags()[0]);
        });
//         3. 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(imageName);
        // 3.1 配置容器环境限制
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(1024 * 1024 * 1024L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpusetCpus("0");
        hostConfig.withSecurityOpts(List.of("no-new-privileges"));
        // 服务器路径与容器内部路径映射
        hostConfig.setBinds(new Bind("/home/lily/oj-codeSandbox", new Volume("/app")));
//         3.2 配置容器挂载
//         交互式容器创建命令
        containerCmd.withBinds();
        CreateContainerResponse containerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .exec();
        // 4. 查看容器
        // {"containerId":"5af9c3b31b879bc3de6e03621959045762ceb41fde0f1c8acdcdab0f03b918c1"
        String containerId = "5af9c3b31b879bc3de6e03621959045762ceb41fde0f1c8acdcdab0f03b918c1";
//        System.out.println(JSONUtil.toJsonStr(containerResponse));
        System.out.printf("容器ID: %s\n", containerId);
        // 5. 启动容器
//        dockerClient.startContainerCmd(containerId).exec();
//        String containerId = "44762fb31a6f15042d558cf6c19f2c775e63f822b34ebf60e6dbf37158b7d7ba";
        // 执行命令并获取结果

//        String[] command = {"java", "-version"};
        String[] command = {"ls", "/app/execCode"};
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(command)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withAttachStdin(true)
                .exec();

        System.out.println("创建执行命令" + JSONUtil.toJsonStr(execCreateCmdResponse));

//        创建执行命令{"id":"412cb197ae2d7e5218c6165eb9618e19992e2a4180714a005e597ad1be8f869a"}
        String execId = execCreateCmdResponse.getId();

        ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
            @Override
            public void onComplete() {
                // 如果执行完成，则表示没超时
                System.out.println("完成了");
//                timeout[0] = false;
                super.onComplete();
            }

            @Override
            public void onNext(Frame frame) {
                StreamType streamType = frame.getStreamType();
                if (StreamType.STDOUT.equals(streamType)) {
                    System.out.println("执行无误");
                }
                System.out.println(JSONUtil.toJsonStr(frame));
                byte[] payload = frame.getPayload();
                String result = new String(payload, StandardCharsets.UTF_8);
                System.out.println("输出结果：" + result);
                super.onNext(frame);
            }
        };

        System.out.println("调用回调函数开始。。。");
        ExecStartResultCallback resultCallback = null;
        try {
            resultCallback = dockerClient.execStartCmd(execId)
                    .exec(execStartResultCallback)
                    .awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("resultCallback: " + resultCallback.toString());
    }


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        // 1. 把用户的代码保存为文件 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
        // 把用户的代码隔离存放
        if (!FileUtil.exist(codeDirPath)) {
            FileUtil.mkdir(codeDirPath);
        }
        // 新建用户代码文件
        File userCodeFile = FileUtil.writeString(code, codeFilePath, StandardCharsets.UTF_8);
        // 连接docker服务器 获得dockerClient
        DockerClient dockerClient = DockerClientBuilder
                .getInstance("tcp://192.168.70.130:2375").build();
        // 2. 拉取镜像
        String imageName = "openjdk:8-alpine";

        String containerId = "5af9c3b31b879bc3de6e03621959045762ceb41fde0f1c8acdcdab0f03b918c1";
        /**
         * 编译运行
         */
//        String[] command = {"ls", "/app/execCode"};
//        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
//                .withCmd(command)
//                .withAttachStderr(true)
//                .withAttachStdout(true)
//                .withAttachStdin(true)
//                .exec();
//
//        String execId = execCreateCmdResponse.getId();
//
//        ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
//            @Override
//            public void onComplete() {
//                System.out.println("完成了");
//                super.onComplete();
//            }
//
//            @Override
//            public void onNext(Frame frame) {
//                StreamType streamType = frame.getStreamType();
//
//                System.out.println("查看编译结果： "+streamType);
//                if (StreamType.STDOUT.equals(streamType)) {
//                    System.out.println("执行无误: "+streamType);
//                }
//                byte[] payload = frame.getPayload();
//                String result = new String(payload, StandardCharsets.UTF_8);
//                System.out.println("编译输出结果：" + result);
//                super.onNext(frame);
//            }
//        };
//
//        ExecStartResultCallback resultCallback = null;
//        try {
//            resultCallback = dockerClient.execStartCmd(execId)
//                    .exec(execStartResultCallback)
//                    .awaitCompletion();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("resultCallback: " + resultCallback.toString());
        // 2. 编译代码获取执行结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse = doCompile(userCodeFile.getAbsolutePath());
        Integer compileStatus = executeCodeResponse.getCodeSandboxStatus();
        if (compileStatus == ExecuteStatusEnum.COMPILE_SUCCESS.getExecuteStatus()) {
        // 3. 编译成功即执行程序
            executeCodeResponse = doRun(dockerClient, containerId, inputList, codeDirPath);

        }

//        // 文件删除
        if (FileUtil.exist(codeDirPath)) {
            boolean del = FileUtil.del(codeDirPath);
            log.info("删除文件夹：" + codeDirPath + " " + (del ? "成功" : "失败"));
        }
        return executeCodeResponse;
    }

    private ExecuteCodeResponse doRun(DockerClient dockerClient, String containerId, List<String> inputList, String codeDirPath) {

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();

        List<CodeOutput> codeOutputList = new ArrayList<>();

        Long maxTime = 0L;

        // 计时器
        StopWatch stopWatch = new StopWatch();


        // 运行代码
        // docker exec 5af9c3b31b87 java -cp /app/execCode Main 2 3
        // todo
        String[] runRootCmd = {"java", "-cp", codeDirPath, "Main"};
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
            String inputExample = inputArgs;

            /**
             * 执行结果回调函数定义
             */
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    String terminalOutput = new String(frame.getPayload(), StandardCharsets.UTF_8);
                    if (StreamType.STDERR.equals(streamType)) {
                        stdErrorMessage[0] = terminalOutput;
                        log.info(terminalOutput + "失败");
                    } else {
                        stdoutMessage[0] = terminalOutput;
                        log.error(terminalOutput + "成功");
                    }
                    System.out.println("输出结果：" + terminalOutput);
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
                        .awaitCompletion(TIME_OUT, TimeUnit.MICROSECONDS);
                stopWatch.stop();
            } catch (InterruptedException e) {
                // todo 程序异常
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }

            maxTime = Math.max(maxTime, stopWatch.getLastTaskTimeMillis());
            codeOutput.setStdoutMessage(stdoutMessage[0]);
            codeOutput.setStdErrorMessage(stdErrorMessage[0]);
            codeOutput.setInputExample(inputExample);
            codeOutputList.add(codeOutput);

            /**
             * 获取占用的内存
             */
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {

                @Override
                public void onNext(Statistics statistics) {
//                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
//                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
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
            statsCmd.exec(statisticsResultCallback);
            statsCmd.close();

        }
        // todo 收集结果
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
        return executeCodeResponse;
    }


    public ExecuteCodeResponse doCompile(String codeFilePath) {
        // TODO
//        2. 编译代码获取执行结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
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
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                bufferedReader.lines().forEach(line -> outputMessage.append(line).append("\n"));
                executeCodeResponse.setCodeSandboxMes(outputMessage.toString()); // 编译信息
                executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.COMPILE_SUCCESS.getExecuteStatus()); // 编译成功状态
                log.info("编译成功");
            } else {
                // 分批获取程序的异常输出
                StringBuilder errorMessage = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
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

    /**
     * 系统异常处理
     */
    private ExecuteCodeResponse getErrorResponse(Exception e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setCodeSandboxMes(e.getMessage());
        executeCodeResponse.setCodeSandboxStatus(ExecuteStatusEnum.SYSTEM_ERROR.getExecuteStatus());
        return executeCodeResponse;
    }

}
