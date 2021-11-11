package com.developcollect.cache.lock;

import com.developcollect.core.lang.init.Initable;
import com.developcollect.core.thread.lock.CacheLock;
import com.developcollect.core.utils.IdUtil;
import com.developcollect.core.utils.StrUtil;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.Subscription;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

@Slf4j
public class RedisCacheLock implements CacheLock, Initable {
    private static final String UNLOCK_MESSAGE = "0";
    private static StringRedisTemplate stringRedisTemplate;
    private static final String SERVER_IDENTITY = IdUtil.fastSimpleUUID();
    private static final ConcurrentMap<String, ExpirationEntry> EXPIRATION_RENEWAL_MAP = new ConcurrentHashMap<>();
    private static HashedWheelTimer timer;
    private final long internalLockLeaseTime = TimeUnit.SECONDS.toMillis(30);


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
     * 强制解锁脚本
     */
    private static final DefaultRedisScript<Long> FORCE_UNLOCK_REDIS_SCRIPT = new DefaultRedisScript<>(
            "if (redis.call('del', KEYS[1]) == 1) then "
                    + "redis.call('publish', KEYS[2], ARGV[1]); "
                    + "return 1 "
                    + "else "
                    + "return 0 "
                    + "end",
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
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("key不能为空");
        }
        this.key = key;
    }

    private RedisCacheLock() {
        this.key = "";
    }

    @Override
    public void init(Object... args) {
        RedisCacheLock.stringRedisTemplate = (StringRedisTemplate) args[0];
        initTimer();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void lock() {
        try {
            this.lock(false);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }


    private void lock(boolean interruptibly) throws InterruptedException {
        long threadId = Thread.currentThread().getId();
        Long ttl = tryAcquire(threadId);
        // lock acquired
        if (ttl == null) {
            return;
        }

        // 执行订阅
        Semaphore semaphore = subscribe();

        try {
            while (true) {
                ttl = tryAcquire(threadId);
                // lock acquired
                if (ttl == null) {
                    break;
                }

                if (ttl >= 0) {
                    try {
                        semaphore.tryAcquire(ttl, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        if (interruptibly) {
                            throw e;
                        }
                        semaphore.tryAcquire(ttl, TimeUnit.MILLISECONDS);
                    }
                } else {
                    if (interruptibly) {
                        semaphore.acquire();
                    } else {
                        semaphore.acquireUninterruptibly();
                    }
                }
            }
        } finally {
            // 退订
            unsubscribe();
        }
    }

    /**
     * 尝试上锁方法
     * 这个方法会执行上锁脚本尝试上锁，如果上锁成功那么会启动锁延时定时任务
     *
     * @param threadId 线程id
     * @return 上锁成功返回null，上锁失败则返回已有锁的ttl
     */
    private Long tryAcquire(long threadId) {
        Long ttl = stringRedisTemplate.execute(
                TRY_LOCK_REDIS_SCRIPT, Collections.singletonList(getKey()),
                Long.toString(internalLockLeaseTime), getLockValue(threadId)
        );

        // 上锁成功，安排上自动延时定时任务
        if (ttl == null) {
            scheduleExpirationRenewal(threadId);
        }
        return ttl;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.lock(true);
    }

    @Override
    public boolean tryLock() {
        Long ttl = tryAcquire(Thread.currentThread().getId());
        return ttl == null;
    }

    @Override
    public boolean tryLock(long waitTime, TimeUnit unit) throws InterruptedException {
        long time = unit.toMillis(waitTime);
        long current = System.currentTimeMillis();
        long threadId = Thread.currentThread().getId();
        Long ttl = tryAcquire(threadId);
        // lock acquired
        if (ttl == null) {
            return true;
        }

        time -= System.currentTimeMillis() - current;
        if (time <= 0) {
            return false;
        }

        Semaphore semaphore = subscribe();

        try {
            time -= System.currentTimeMillis() - current;
            if (time <= 0) {
                return false;
            }

            while (true) {
                long currentTime = System.currentTimeMillis();
                ttl = tryAcquire(threadId);
                // lock acquired
                if (ttl == null) {
                    return true;
                }

                time -= System.currentTimeMillis() - currentTime;
                if (time <= 0) {
                    return false;
                }

                // waiting for message
                currentTime = System.currentTimeMillis();
                if (ttl >= 0 && ttl < time) {
                    semaphore.tryAcquire(ttl, TimeUnit.MILLISECONDS);
                } else {
                    semaphore.tryAcquire(time, TimeUnit.MILLISECONDS);
                }

                time -= System.currentTimeMillis() - currentTime;
                if (time <= 0) {
                    return false;
                }
            }
        } finally {
            unsubscribe();
        }
    }

    @Override
    public void unlock() {
        unlock(getKey(), Thread.currentThread().getId());
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("RedisCacheLock`s newCondition method is unsupported");
    }


    @Override
    public boolean forceUnlock() {
        return forceUnlock(getKey(), Thread.currentThread().getId());
    }

    @Override
    public boolean isLocked() {
        return Boolean.TRUE.equals(stringRedisTemplate.execute((RedisCallback<Boolean>) redisConnection -> redisConnection.exists(getKey().getBytes())));
    }

    @Override
    public boolean isHeldByThread(long threadId) {
        return stringRedisTemplate.opsForHash().get(getKey(), getLockValue(threadId)) != null;
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return isHeldByThread(Thread.currentThread().getId());
    }

    @Override
    public int getHoldCount() {
        Object o = stringRedisTemplate.opsForHash().get(getKey(), getLockValue(Thread.currentThread().getId()));
        if (o == null) {
            return 0;
        }
        return Integer.parseInt(o.toString());
    }

    /**
     * 释放锁
     *
     * @param key key
     * @return boolean 是否成功
     * @author Zhu Kaixiao
     * @date 2019/11/14 9:26
     */
    private boolean unlock(String key, long threadId) {
        Long result = stringRedisTemplate.execute(
                UNLOCK_REDIS_SCRIPT,
                Arrays.asList(key, getChannelName()),
                UNLOCK_MESSAGE, Long.toString(internalLockLeaseTime), getLockValue(threadId));

        boolean unlocked = Objects.equals(result, 1L);

        // 解锁成功了，取消掉自动延时定时任务
        if (unlocked) {
            cancelExpirationRenewal(threadId);
        }

        // 返回null，说明如果锁不存在或锁不属于当前线程
        if (result == null) {
            throw new IllegalMonitorStateException("attempt to unlock lock, not locked by current thread by thread-id: " + threadId);
        }
        return unlocked;
    }

    /**
     * 强制释放锁
     * 直接执行del命令，不判断锁的拥有者
     *
     * @param key      key
     * @param threadId 线程id
     */
    private boolean forceUnlock(String key, long threadId) {
        Long result = stringRedisTemplate.execute(
                FORCE_UNLOCK_REDIS_SCRIPT,
                Arrays.asList(key, getChannelName()),
                UNLOCK_MESSAGE, Long.toString(internalLockLeaseTime), getLockValue(threadId));
        boolean unlocked = Objects.equals(result, 1L);
        // 解锁成功了，取消掉自动延时定时任务
        if (unlocked) {
            cancelExpirationRenewal(threadId);
        }
        return unlocked;
    }


    /**
     * 为指定线程id创建自动延时定时任务
     *
     * @param threadId 线程id
     */
    private void scheduleExpirationRenewal(long threadId) {
        ExpirationEntry entry = new ExpirationEntry();
        ExpirationEntry oldEntry = EXPIRATION_RENEWAL_MAP.putIfAbsent(getEntryName(), entry);
        if (oldEntry != null) {
            oldEntry.addThreadId(threadId);
        } else {
            entry.addThreadId(threadId);
            renewExpiration();
        }
    }

    /**
     * 取消指定线程id的自动延时定时任务
     *
     * @param threadId 线程id
     */
    private void cancelExpirationRenewal(Long threadId) {
        ExpirationEntry task = EXPIRATION_RENEWAL_MAP.get(getEntryName());
        if (task == null) {
            return;
        }

        if (threadId != null) {
            task.removeThreadId(threadId);
        }

        if (threadId == null || task.hasNoThreads()) {
            Timeout timeout = task.getTimeout();
            if (timeout != null) {
                timeout.cancel();
            }
            EXPIRATION_RENEWAL_MAP.remove(getEntryName());
        }
    }

    /**
     * 启动自动延时定时任务
     */
    private void renewExpiration() {
        ExpirationEntry ee = EXPIRATION_RENEWAL_MAP.get(getEntryName());
        if (ee == null) {
            return;
        }

        Timeout timeout = newTimeout(t -> {
            ExpirationEntry ent = EXPIRATION_RENEWAL_MAP.get(getEntryName());
            if (ent == null) {
                return;
            }
            Long threadId = ent.getFirstThreadId();
            if (threadId == null) {
                return;
            }

            try {
                if (renewExpiration(threadId)) {
                    // reschedule itself
                    renewExpiration();
                }
            } catch (Exception e) {
                log.error("Can't update lock " + getKey() + " expiration", e);
                return;
            }
        }, internalLockLeaseTime / 3, TimeUnit.MILLISECONDS);

        ee.setTimeout(timeout);
    }

    /**
     * 执行自动延时脚本
     *
     * @param threadId 线程id
     * @return 是否延时成功
     */
    private Boolean renewExpiration(long threadId) {
        // 执行延时脚本
        Long result = stringRedisTemplate.execute(RENEW_EXPIRATION_REDIS_SCRIPT,
                Collections.singletonList(getKey()),
                Long.toString(internalLockLeaseTime), getLockValue(threadId));
        return Objects.equals(1L, result);
    }


    private String getEntryName() {
        return SERVER_IDENTITY + ":" + getKey();
    }

    private static String getLockValue(long threadId) {
        return SERVER_IDENTITY + ":" + threadId;
    }


    private static class ExpirationEntry {

        private final Map<Long, Integer> threadIds = new LinkedHashMap<>();
        private volatile Timeout timeout;

        public ExpirationEntry() {
            super();
        }

        public void addThreadId(long threadId) {
            Integer counter = threadIds.get(threadId);
            if (counter == null) {
                counter = 1;
            } else {
                counter++;
            }
            threadIds.put(threadId, counter);
        }

        public boolean hasNoThreads() {
            return threadIds.isEmpty();
        }

        public Long getFirstThreadId() {
            if (threadIds.isEmpty()) {
                return null;
            }
            return threadIds.keySet().iterator().next();
        }

        public void removeThreadId(long threadId) {
            Integer counter = threadIds.get(threadId);
            if (counter == null) {
                return;
            }
            counter--;
            if (counter == 0) {
                threadIds.remove(threadId);
            } else {
                threadIds.put(threadId, counter);
            }
        }


        public void setTimeout(Timeout timeout) {
            this.timeout = timeout;
        }

        public Timeout getTimeout() {
            return timeout;
        }

    }


    /**
     * 启动一个任务
     *
     * @param task  任务
     * @param delay 延时时长
     * @param unit  延时单位
     * @return Timeout
     */
    private static Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        try {
            return timer.newTimeout(task, delay, unit);
        } catch (IllegalStateException e) {
            throw e;
        }
    }

    /**
     * 初始化定时器
     */
    private static void initTimer() {
        timer = new HashedWheelTimer(new DefaultThreadFactory("dc-RedisCacheLock-timer", true),
                100, TimeUnit.MILLISECONDS, 1024, false);
    }


    /**
     * 订阅锁释放事件
     */
    private Semaphore subscribe() {
        Semaphore semaphore = new Semaphore(0);
        stringRedisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.subscribe((message, pattern) -> semaphore.release(1), getChannelName().getBytes());
            return null;
        });
        return semaphore;
    }

    /**
     * 退订锁释放事件
     */
    private void unsubscribe() {
        stringRedisTemplate.execute((RedisCallback<Object>) connection -> {
            Subscription subscription = connection.getSubscription();
            if (subscription != null) {
                subscription.unsubscribe(getChannelName().getBytes());
            }
            return null;
        });
    }

    /**
     * 获取事件渠道名称
     *
     * @return 事件渠道名称
     */
    private String getChannelName() {
        return prefixName("dc:lock_channel", getKey());
    }

    private static String prefixName(String prefix, String name) {
        if (name.contains("{")) {
            return prefix + ":" + name;
        }
        return prefix + ":{" + name + "}";
    }
}
