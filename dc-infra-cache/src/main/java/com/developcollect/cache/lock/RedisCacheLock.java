package com.developcollect.cache.lock;

import com.developcollect.core.lang.init.Initable;
import com.developcollect.core.thread.lock.CacheLock;
import com.developcollect.core.utils.ServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class RedisCacheLock implements CacheLock, Initable {
    private static StringRedisTemplate stringRedisTemplate;
    private static final String SERVER_IDENTITY = Long.toHexString(ServerUtil.getServerIdentity());


    /**
     * 延期脚本
     */
    private static final DefaultRedisScript<Long> RENEW_EXPIRATION_REDIS_SCRIPT = new DefaultRedisScript<>(
            // 如果锁存在，就延期

            "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
                    "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                    "return 1; " +
                    "end; " +
                    "return 0;",
            Long.class
    );

    /**
     * 解锁脚本
     */
    private static final DefaultRedisScript<Long> UNLOCK_REDIS_SCRIPT = new DefaultRedisScript<>(
            // 如果锁不存在，直接返回
            "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then " +
                      "return nil;" +
                    "end; " +
                    // 给锁的重入次数减1，如果减了之后的数 > 0，说明依然持锁，更新过期时间
                    // 如果减了之后的数不大于0，则删除key(解锁)，并发布解锁消息
                    "local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); " +
                    "if (counter > 0) then " +
                      "redis.call('pexpire', KEYS[1], ARGV[2]); " +
                      "return 0; " +
                    "else " +
                      "redis.call('del', KEYS[1]); " +
                      "redis.call('publish', KEYS[2], ARGV[1]); " +
                      "return 1; " +
                    "end; " +
                    "return nil;",
            Long.class
    );


    /**
     * 上锁脚本
     */
    private static final DefaultRedisScript<Long> TRY_LOCK_REDIS_SCRIPT = new DefaultRedisScript<>(
            // 如果key不存在，则上锁，并设置过期时间
            "if (redis.call('exists', KEYS[1]) == 0) then " +
                    "redis.call('hset', KEYS[1], ARGV[2], 1); " +
                    "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                    "return nil; " +
                    "end; " +
                    // 如果key存在，并且锁名相同(表示是当前服务的当前线程上的锁),则把值自增(实现锁重入)
                    "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
                    "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
                    "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                    "return nil; " +
                    "end; " +
                    // 否则就上锁失败，返回锁上当前的ttl
                    "return redis.call('pttl', KEYS[1]);",
            Long.class
    );


    private final String key;

    public RedisCacheLock(String key) {
        this.key = key;
    }

    @Override
    public void init(Object... args) {
        RedisCacheLock.stringRedisTemplate = (StringRedisTemplate) args[0];
        stringRedisTemplate.execute((RedisCallback<String>) connection -> {
            connection.pSubscribe(new MessageListener() {
                @Override
                public void onMessage(Message message, byte[] pattern) {
                    // todo
                    log.info("监听到锁释放事件，则立即唤醒等待线程");
                }
            });
            return null;
        });
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
        Long ttl = stringRedisTemplate.execute(
                TRY_LOCK_REDIS_SCRIPT, Collections.singletonList(key),
                getLockName(Thread.currentThread().getId()), Long.toString(seconds)
        );
        return ttl == null;
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
        Long result = stringRedisTemplate.execute(UNLOCK_REDIS_SCRIPT, Collections.singletonList(key),
                getLockName(Thread.currentThread().getId()));
        return Objects.equals(result, 1L);
    }

    private static String getLockName(long threadId) {
        return SERVER_IDENTITY + ":" + threadId;
    }


}
