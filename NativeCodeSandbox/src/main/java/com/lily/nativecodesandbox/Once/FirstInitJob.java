package com.lily.nativecodesandbox.Once;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Arrays;

/**
 * Created by lily via on 2024/4/19 9:54
 */
public class FirstInitJob {

//    @Value("${Docker.host:127.0.0.1}")
    public static String host = "192.168.70.130";

//    @Value("${Docker.host:2375}")
    public static String port = "2375";

//    @Value("${Docker.Bind.path}")
    public static String bindPath = File.separator + "home" + File.separator + "lily" + File.separator + "oj-codeSandbox";

//    @Value("${Docker.Bind.volume}")
    public static String volume = File.separator + "app";

    private static Boolean FIRST_INIT = true;

    public static String containerId = "5af9c3b31b879bc3de6e03621959045762ceb41fde0f1c8acdcdab0f03b918c1";

    /**
     * 初始化Docker得到容器id
     */
    public static String InitContainer() {
        if (FIRST_INIT) {
            containerId = firstInitDocker();
            FIRST_INIT = false;
        }
        return containerId;
    }

     private static String firstInitDocker() {
        // 连接docker服务器
        DockerClient dockerClient = DockerClientBuilder
                .getInstance("tcp://" + host + ":" + port).build();
//        // 2. 拉取java8镜像
        String imageName = "openjdk:8-alpine";
        PullImageResultCallback imageResultCallback = dockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback());
        imageResultCallback.awaitSuccess();
//         3. 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(imageName);
        // 3.1 配置容器环境限制
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(1024 * 1024 * 1024L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpusetCpus("0");
        hostConfig.withSecurityOpts(Arrays.asList("no-new-privileges"));
        // 服务器路径与容器内部路径映射
        hostConfig.setBinds(new Bind(bindPath, new Volume(volume)));
//         3.2 配置容器挂载  交互式容器创建命令
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
        // 4. 查看容器Id
        String containerId = containerResponse.getId();
        System.out.printf("容器ID: %s\n", containerId);
        return containerId;
    }
}
