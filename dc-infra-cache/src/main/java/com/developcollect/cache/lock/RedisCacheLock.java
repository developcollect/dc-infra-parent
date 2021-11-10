package com.developcollect.cache.lock;

import com.developcollect.core.lang.init.Initable;
import com.developcollect.core.thread.lock.CacheLock;
import com.developcollect.core.utils.ServerUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;

public class RedisCacheLock implements CacheLock, Initable {
    private static StringRedisTemplate stringRedisTemplate;
    private static final String SERVER_IDENTITY = Long.toHexString(ServerUtil.getServerIdentity());


    // 延期
    //                "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
    //                    "redis.call('pexpire', KEYS[1], ARGV[1]); " +
    //                    "return 1; " +
    //                "end; " +
    //                "return 0;",

    /**
     * 通过lua脚本保证释放锁的操作具有原子性
     */
//                    "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then " +
//                    "return nil;" +
//                "end; " +
//                "local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); " +
//                "if (counter > 0) then " +
//                    "redis.call('pexpire', KEYS[1], ARGV[2]); " +
//                    "return 0; " +
//                "else " +
//                    "redis.call('del', KEYS[1]); " +
//                    "redis.call('publish', KEYS[2], ARGV[1]); " +
//                    "return 1; "+
//                "end; " +
//                "return nil;",

    private static final DefaultRedisScript<Long> releaseLockRedisScript = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] " +
                    "then " +
                    "  return redis.call('del', KEYS[1]) " +
                    "else " +
                    "  return 0 " +
                    "end",
            Long.class
    );
// "if (redis.call('exists', KEYS[1]) == 0) then " +
//                      "redis.call('hset', KEYS[1], ARGV[2], 1); " +
//                      "redis.call('pexpire', KEYS[1], ARGV[1]); " +
//                      "return nil; " +
//                  "end; " +
//                  "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
//                      "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
//                      "redis.call('pexpire', KEYS[1], ARGV[1]); " +
//                      "return nil; " +
//                  "end; " +
//                  "return redis.call('pttl', KEYS[1]);"

    private static final DefaultRedisScript<Boolean> lockRedisScript = new DefaultRedisScript<>(
            "local v = redis.call('get', KEYS[1]) " +
                    "if (v==false or v==ARGV[1]) " +
                    "then " +
                    "  redis.call('set', KEYS[1], ARGV[1])  " +
                    "  redis.call('expire', KEYS[1], ARGV[2]) " +
                    "  return true " +
                    "else  " +
                    "  return false " +
                    "end ",
            Boolean.class
    );

    private final String key;

    public RedisCacheLock(String key) {
        this.key = key;
    }

    @Override
    public void init(Object... args) {
        RedisCacheLock.stringRedisTemplate = (StringRedisTemplate) args[0];
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void lock() {
        while (true) {
            if (tryLock()) {
                return;
            }
            // todo 线程休眠改为监听锁释放事件
            LockSupport.parkNanos(this, 1000);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        while (true) {
            if (tryLock()) {
                return;
            }
            LockSupport.parkNanos(this, 1000);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public boolean tryLock() {
        return tryLock(getKey());
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long nanosTimeout = unit.toNanos(time);
        if (nanosTimeout <= 0L) {
            return false;
        }
        long deadline = System.nanoTime() + nanosTimeout;
        while (true) {
            if (tryLock()) {
                return true;
            }

            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L) {
                return false;
            }
            // 剩余超时时长超过1秒才进行线程休眠
            if (nanosTimeout > 1000L) {
                LockSupport.parkNanos(this, 1000);
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public void unlock() {
        unlock(getKey());
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("RedisCacheLock`s newCondition method is unsupported");
    }


    private static boolean tryLock(String key) {
        return tryLock(key, 30, TimeUnit.SECONDS);
    }

    private static boolean tryLock(String key, long expireTime, TimeUnit timeUnit) {
        long seconds = timeUnit.toSeconds(expireTime);
        Boolean execute = stringRedisTemplate.execute(
                lockRedisScript, Collections.singletonList(key),
                getLockName(Thread.currentThread().getId()), Long.toString(seconds)
        );
        return execute;
    }

    /**
     * 释放锁
     *
     * @param key 锁ID
     * @return boolean 是否成功
     * @author Zhu Kaixiao
     * @date 2019/11/14 9:26
     */
    public boolean unlock(String key) {
        Long result = stringRedisTemplate.execute(releaseLockRedisScript, Collections.singletonList(key),
                getLockName(Thread.currentThread().getId()));
        return Objects.equals(result, 1L);
    }

    private static String getLockName(long threadId) {
        return SERVER_IDENTITY + ":" + threadId;
    }


}
