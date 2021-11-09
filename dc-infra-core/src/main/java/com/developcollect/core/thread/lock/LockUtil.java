package com.developcollect.core.thread.lock;


import lombok.extern.slf4j.Slf4j;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/22 14:42
 */
@Slf4j
public class LockUtil extends cn.hutool.core.thread.lock.LockUtil {

    public static CacheLock createCacheLock(String key) {
        return new MapCacheLock(key);
    }

}
