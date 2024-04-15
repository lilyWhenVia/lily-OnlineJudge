package com.lily.nativecodesandbox.common;

import cn.hutool.core.date.DateTime;

/**
 * Created by lily via on 2024/4/15 9:53
 */
public class DaemonTask implements Runnable {
    private volatile boolean flag = true;

    @Override
    public void run() {
        while (flag) {
            // 执行任务
        }
    }

    public void stopThread() {
        flag = false;
    }
}
