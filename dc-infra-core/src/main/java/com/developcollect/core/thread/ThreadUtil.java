package com.developcollect.core.thread;

import java.util.concurrent.Future;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/21 9:44
 */
public class ThreadUtil extends cn.hutool.core.thread.ThreadUtil {
    public static <T> void waitForDone(Future<T> future, long timeout) {
        long t1 = System.currentTimeMillis();
        while (true) {
            if (future.isDone()) {
                return;
            }
            if (timeout > 0 && System.currentTimeMillis() - t1 >= timeout) {
                return;
            }
            sleep(10);
        }
    }

    public static <T> void waitForDone(Future<T> future) {
        waitForDone(future, 0);
    }
}
