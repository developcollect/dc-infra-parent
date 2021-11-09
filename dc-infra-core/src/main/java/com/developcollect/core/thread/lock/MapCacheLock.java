package com.developcollect.core.thread.lock;

import com.developcollect.core.utils.LambdaUtil;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;


@Slf4j
public class MapCacheLock implements CacheLock {

    private static final Map<Object, Thread> threadMap = new ConcurrentHashMap<>();
    private static final Map<Object, Integer> lockCountMap = new ConcurrentHashMap<>();


    private final String key;

    public MapCacheLock(String key) {
        this.key = key.intern();
    }

    @Override
    public String getKey() {
        return key;
    }


    @Override
    public void lock() {
        lock(getKey(), true);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        lock(getKey(), false);
    }

    @Override
    public boolean tryLock() {
        return tryLock(getKey());
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        return tryLock(getKey(), time, unit);
    }

    @Override
    public void unlock() {
        unlock(getKey());
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("MapCacheLock`s newCondition method is unsupported");
    }


    /**
     * 在指定时间内尝试获取锁并锁定，如果获取锁成功，返回true，如果超时仍未获取锁，返回false
     */
    private static boolean tryLock(Object obj, long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        if (nanos <= 0) {
            return false;
        }
        long deadline = System.nanoTime() + nanos;
        synchronized (obj) {
            for (; ; ) {
                if (System.nanoTime() >= deadline) {
                    return false;
                }
                if (tryLock0(obj)) {
                    return true;
                } else {
                    // 等待被唤醒或者超时
                    obj.wait(timeout);
                }
            }
        }
    }

    /**
     * 尝试获取锁并锁定，如果获取锁成功，返回true
     */
    private static boolean tryLock(Object obj) {
        synchronized (obj) {
            return tryLock0(obj);
        }
    }

    /**
     * 获取锁，如果锁被占用则一直等待
     */
    private static void lock(Object obj, boolean ignoreInterruptedException) {
        synchronized (obj) {
            for (; ; ) {
                if (tryLock0(obj)) {
                    return;
                } else {
                    // 等待锁释放后被通知
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        if (!ignoreInterruptedException) {
                            LambdaUtil.raise(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * 释放锁
     */
    private static void unlock(Object obj) {
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


    // region 对参数为String的锁操作进行优化
    /*
     * 当字符串在运行时通过+连接时，对象可能在堆区(具体什么时候在堆区，什么是否在常量池还不清楚)
     * 导致锁定的不是同一个对象，就会导致死锁。因此统一调用intern()获取常量池中的字符串
     */

    private static boolean tryLock(String lockKey, long timeout, TimeUnit unit) throws InterruptedException {
        return tryLock((Object) lockKey.intern(), timeout, unit);
    }


    // endregion

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
