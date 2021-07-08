package com.developcollect.core.thread.lock;


import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/22 14:42
 */
public class LockUtil extends cn.hutool.core.thread.lock.LockUtil {

    private static final Map<Object, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * 尝试获取锁并锁定，如果获取锁成功，返回true
     */
    public static boolean tryLock(Object obj) {
        lock.lock();
        try {
            ReentrantLock lockForObj = lockMap.computeIfAbsent(obj, o -> new ReentrantLock());
            return lockForObj.tryLock();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取锁，如果锁被占用则一直等待
     */
    public static void lock(Object obj) {
        ReentrantLock lockForObj;
        lock.lock();
        try {
            lockForObj = lockMap.computeIfAbsent(obj, o -> new ReentrantLock());
        } finally {
            lock.unlock();
        }

        lockForObj.lock();
    }

    /**
     * 释放锁
     */
    public static void unlock(Object obj) {
        lock.lock();
        try {
            ReentrantLock lockForObj = lockMap.get(obj);
            if (lockForObj == null) {
                throw new IllegalArgumentException("释放锁前需要先上锁：" + obj);
            }
            lockForObj.unlock();
            if (!lockForObj.hasQueuedThreads() && !lockForObj.isLocked()) {
                lockMap.remove(obj);
            }
        } finally {
            lock.unlock();
        }
    }

}
