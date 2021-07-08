package com.developcollect.core.thread.lock;



import com.developcollect.core.utils.LambdaUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/22 14:42
 */
public class LockUtil extends cn.hutool.core.thread.lock.LockUtil {

    private static final Map<Object, Thread> threadMap = new ConcurrentHashMap<>();
    private static final Map<Object, Integer> lockCountMap = new ConcurrentHashMap<>();

    /**
     * 在指定时间内尝试获取锁并锁定，如果获取锁成功，返回true，如果超时仍未获取锁，返回false
     */
    public static boolean tryLock(Object obj, long timeout, TimeUnit unit) {
        long nanos = unit.toNanos(timeout);
        if (nanos <= 0) {
            return false;
        }
        long deadline = System.nanoTime() + nanos;
        synchronized (obj) {
            for (;;) {
                if (System.nanoTime() >= deadline) {
                    return false;
                }
                if (tryLock0(obj)) {
                    return true;
                } else {
                    try {
                        // 等待被唤醒或者超时
                        obj.wait(timeout);
                    } catch (InterruptedException e) {
                        LambdaUtil.raise(e);
                    }
                }
            }
        }
    }

    /**
     * 尝试获取锁并锁定，如果获取锁成功，返回true
     */
    public static boolean tryLock(Object obj) {
        synchronized (obj) {
            return tryLock0(obj);
        }
    }

    /**
     * 获取锁，如果锁被占用则一直等待
     */
    public static void lock(Object obj) {
        synchronized (obj) {
            for (;;) {
                if (tryLock0(obj)) {
                    return;
                } else {
                    try {
                        // 等待锁释放后被通知
                        obj.wait();
                    } catch (InterruptedException e) {
                        // 线程被强制中断， 异常直接向上抛出
                        LambdaUtil.raise(e);
                    }
                }
            }
        }
    }

    /**
     * 释放锁
     */
    public static void unlock(Object obj) {
        synchronized (obj) {
            Thread thread = threadMap.get(obj);
            if (thread == null) {
                throw new IllegalArgumentException("释放锁前需要先上锁：" + obj);
            }
            Thread currentThread = Thread.currentThread();
            if (thread == currentThread) {
                Integer count = lockCountMap.get(obj) - 1;
                if (count == 0) {
                    // 重入次数归零了，说明锁释放了
                    threadMap.remove(obj);
                    lockCountMap.remove(obj);
                    // 通知随机(HotSpot虚拟机的实现其实是取第一个)一个等待锁的线程争抢锁
                    obj.notify();
                } else {
                    // 这里只是把重入次数-1，其实锁还是占用着的
                    lockCountMap.put(obj, count);
                }
            } else {
                throw new IllegalMonitorStateException("当前线程并不持有锁，无法执行释放锁操作");
            }
        }
    }


    private static boolean tryLock0(Object obj) {
        boolean locked;
        Thread currentThread = Thread.currentThread();
        Thread thread = threadMap.computeIfAbsent(obj, k -> currentThread);
        locked = thread == currentThread;
        if (locked) {
            Integer count = Optional.ofNullable(lockCountMap.get(obj)).orElse(0);
            lockCountMap.put(obj, count + 1);
        }
        return locked;
    }
}
