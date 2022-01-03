package com.developcollect.core.lang.retry;

import com.developcollect.core.thread.ThreadUtil;

import java.util.function.Supplier;

public class Retry {

    /**
     * 按时间重试，每次等待100ms
     *
     * @param supplier supplier
     * @param timeout  超时时长
     * @param <T>      返回类型
     * @return 结果
     */
    public static <T> T timeout(Supplier<T> supplier, long timeout) {
        return timeout(supplier, timeout, 100);
    }

    /**
     * 按时间重试
     *
     * @param supplier supplier
     * @param timeout  超时时长
     * @param period   休眠间隔
     * @param <T>      返回类型
     * @return 结果
     */
    public static <T> T timeout(Supplier<T> supplier, long timeout, long period) {
        long begin = System.currentTimeMillis();

        do {
            try {
                T ret = supplier.get();
                if (ret != null) {
                    return ret;
                }
                if (System.currentTimeMillis() - begin >= timeout) {
                    throw new RuntimeTimeoutException("重试超时：" + timeout);
                }
                ThreadUtil.sleep(period);
            } catch (Exception ignore) {
            }
        } while (true);
    }


    /**
     * 按次数重试，每次等待100ms
     *
     * @param supplier supplier
     * @param times    次数
     * @param <T>      返回类型
     * @return 结果
     */
    public static <T> T times(Supplier<T> supplier, long times) {
        return times(supplier, times, 100);
    }

    /**
     * 按次数重试
     *
     * @param supplier supplier
     * @param times    次数
     * @param period   休眠间隔
     * @param <T>      返回类型
     * @return 结果
     */
    public static <T> T times(Supplier<T> supplier, long times, long period) {
        long begin = 0;

        do {
            try {
                T ret = supplier.get();
                if (ret != null) {
                    return ret;
                }
                if (++begin >= times) {
                    throw new RuntimeException("重试超过次数：" + times);
                }
                ThreadUtil.sleep(period);
            } catch (Exception ignore) {
            }
        } while (true);
    }
}
