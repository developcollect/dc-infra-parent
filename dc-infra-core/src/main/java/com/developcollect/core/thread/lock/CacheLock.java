package com.developcollect.core.thread.lock;

import java.util.concurrent.locks.Lock;

/**
 * 基于缓存实现的锁
 */
public interface CacheLock extends Lock {

    /**
     * 加锁和放锁时只认线程和这个key
     * 只要key相同，即便重新new的CacheLock对象也可以继续加锁和放锁
     *
     * @return key
     */
    String getKey();


}
