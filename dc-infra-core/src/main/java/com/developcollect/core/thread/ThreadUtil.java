package com.developcollect.core.thread;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/21 9:44
 */
public class ThreadUtil extends cn.hutool.core.thread.ThreadUtil {

    /**
     * 全局的定时任务线程池
     */
    private static class GlobalSchHolder {
        static final ScheduledExecutorService GLOBAL_SCH = new ScheduledThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                ThreadFactoryBuilder.create().setDaemon(true).setNamePrefix("DcInfra-GlobalSch-").build()
        );
    }


    /**
     * 等待future完成
     *
     * @param future  future
     * @param timeout 最大等待时长
     */
    public static <T> void sleepTillDone(Future<T> future, long timeout) {
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

    /**
     * 等待future完成
     *
     * @param future future
     */
    public static <T> void sleepTillDone(Future<T> future) {
        sleepTillDone(future, 0);
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

    /**
     * 获取当前线程id
     *
     * @return 当前线程id
     */
    public static long currentThreadId() {
        return Thread.currentThread().getId();
    }

    /**
     * 获取当前线程名称
     *
     * @return 当前线程名称
     */
    public static String currentThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * 全局的定时任务线程池
     */
    public static ScheduledExecutorService globalSch() {
        return GlobalSchHolder.GLOBAL_SCH;
    }

}
