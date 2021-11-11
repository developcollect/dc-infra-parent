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

    /**
     * 强制释放锁
     *
     * @return 如果锁存在并且已经释放则返回true，否则返回false
     */
    boolean forceUnlock();

    /**
     * 检查此锁是否被任意线程锁定
     *
     * @return <code>true</code> if locked otherwise <code>false</code>
     */
    boolean isLocked();

    /**
     * 检查指定线程是否持有此锁
     *
     * @param threadId 线程id
     * @return 如果由指定线程持有返回true，否则返回false
     */
    boolean isHeldByThread(long threadId);

    /**
     * 检查当前线程是否持有此锁
     *
     * @return 如果由当前线程持有返回true，否则返回false
     */
    boolean isHeldByCurrentThread();

    /**
     * 当前线程持有该锁的次数，如果当前线程不持有该锁则返回0
     *
     * @return 当前线程持有该锁的次数，如果当前线程不持有该锁则返回0
     */
    int getHoldCount();


}
