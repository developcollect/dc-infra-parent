package com.developcollect.core.thread.lock;


import com.developcollect.core.lang.init.Initable;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/22 14:42
 */
@Slf4j
public class LockUtil extends cn.hutool.core.thread.lock.LockUtil implements Initable {
    private static Function<String, CacheLock> CACHE_LOCK_CREATOR = MapCacheLock::new;

    @SuppressWarnings("unchecked")
    @Override
    public void init(Object... args) {
        CACHE_LOCK_CREATOR = (Function<String, CacheLock>) args[0];
    }

    public static CacheLock createCacheLock(String key) {
        return CACHE_LOCK_CREATOR.apply(key);
    }


    /**
     * 尝试获取锁，在原生方法抛出InterruptedException异常时，也返回false
     * @param lock 锁对象
     * @param time 超时时长
     * @param unit 时长单位
     * @return 是否锁定成功
     */
    public static boolean tryLock(Lock lock, long time, TimeUnit unit) {
        try {
            return lock.tryLock(time, unit);
        } catch (InterruptedException ignore) {
            return false;
        }
    }
}
