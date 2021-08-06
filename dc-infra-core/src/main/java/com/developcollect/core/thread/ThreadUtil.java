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

    public static void wait(Object obj) {
        synchronized (obj) {
            try {
                obj.wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public static void wait(Object obj, long timeout) {
        synchronized (obj) {
            try {
                obj.wait(timeout);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public static void wait(Object obj, long timeout, int nanos) {
        synchronized (obj) {
            try {
                obj.wait(timeout, nanos);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public static long currentThreadId() {
        return Thread.currentThread().getId();
    }

    public static String currentThreadName() {
        return Thread.currentThread().getName();
    }

}
