package com.developcollect.core.thread.lock;


import com.developcollect.core.lang.init.Initable;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/22 14:42
 */
@Slf4j
public class LockUtil extends cn.hutool.core.thread.lock.LockUtil implements Initable {
    private static Function<String, Lock> CACHE_LOCK_CREATOR = MapCacheLock::new;

    @SuppressWarnings("unchecked")
    @Override
    public void init(Object... args) {
        CACHE_LOCK_CREATOR = (Function<String, Lock>) args[0];
    }

    public static Lock createCacheLock(String key) {
        return CACHE_LOCK_CREATOR.apply(key);
    }

}
