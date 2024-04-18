package com.lily.nativecodesandbox.JavaSandbox.impl;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by lily via on 2024/4/15 12:11
 */
@SpringBootTest
class JavaCodeSandboxTest {


    public static void stopWatchTest(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("task1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        stopWatch.stop();
        long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
        System.out.printf("totalTimeMillis: %d\n", lastTaskTimeMillis);
    }

    public static void main(String[] args) {
        String codeFile = "D:\\JavaProject\\OnlineJudge\\NativeCodeSandbox\\execCode";
        new JavaCodeSandboxTest().getProcessByCmd("D:\\JavaProject\\OnlineJudge\\NativeCodeSandbox", codeFile);
    }



    public void getProcessByCmd(String logFilePath, String codeFilePath) {
        // TODO
        ProcessBuilder pb = new ProcessBuilder("java","-Dfile.encoding=UTF-8","Main", "1", "2");
        System.setProperty("console.encoding", "GBK");
        Map<String, String> env = pb.environment();
        env.put("encoding", "UTF-8");
        File file = new File(codeFilePath);
        pb.directory(file);
        String userLogFile = logFilePath + File.separator + "myLog";
        File codeLog = new File(userLogFile);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(codeLog));
        try {
            Process p = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}