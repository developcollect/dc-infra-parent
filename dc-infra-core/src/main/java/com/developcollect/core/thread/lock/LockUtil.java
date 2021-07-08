package com.developcollect.core.thread.lock;



import com.developcollect.core.utils.LambdaUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/22 14:42
 */
public class LockUtil extends cn.hutool.core.thread.lock.LockUtil {

    private static final Map<Object, Thread> threadMap = new ConcurrentHashMap<>();
    private static final Map<Object, Integer> lockCountMap = new ConcurrentHashMap<>();

    /**
     * 尝试获取锁并锁定，如果获取锁成功，返回true
     */
    public static boolean tryLock(Object obj) {
        synchronized (obj) {
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

    /**
     * 获取锁，如果锁被占用则一直等待
     */
    public static void lock(Object obj) {
        boolean locked;

        synchronized (obj) {
            do {
                Thread currentThread = Thread.currentThread();
                Thread thread = threadMap.computeIfAbsent(obj, k -> currentThread);
                locked = thread == currentThread;
                if (locked) {
                    Integer count = Optional.ofNullable(lockCountMap.get(obj)).orElse(0);
                    lockCountMap.put(obj, count + 1);
                } else {
                    try {
                        // 等待锁释放后被通知
                        obj.wait();
                    } catch (InterruptedException e) {
                        LambdaUtil.raise(e);
                    }
                }
            } while (!locked);
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

}
