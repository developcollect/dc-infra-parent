package com.developcollect.cache;

import com.developcollect.core.lang.SystemClock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RedisTemplateCache<K, V> implements Cache<K, V> {

    private static final String RECORD_TIME_KEY_PREFIX = "REDIS_KEY_STORE_TIME:";
    private final RedisTemplate<K, V> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisTemplateCache(RedisTemplate<K, V> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void set(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }


    @Override
    public void set(K key, V value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    @Override
    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean del(K key) {
        Boolean delete = redisTemplate.delete(key);
        return Optional.ofNullable(delete).orElse(false);
    }

    @Override
    public void setAndRecordTime(K key, V value) {
        // todo 改用lua，实现原子性
        redisTemplate.opsForValue().set(key, value);
        stringRedisTemplate.opsForValue().set(RECORD_TIME_KEY_PREFIX + key, Long.toString(System.currentTimeMillis()));
    }


    @Override
    public void setAndRecordTime(K key, V value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        stringRedisTemplate.opsForValue().set(RECORD_TIME_KEY_PREFIX + key, Long.toString(System.currentTimeMillis()), timeout, timeUnit);
    }

    @Override
    public long getAge(K key, TimeUnit timeUnit) {
        String storeTime = stringRedisTemplate.opsForValue().get(RECORD_TIME_KEY_PREFIX + key);
        if (storeTime == null) {
            return -1;
        }
        long st = Long.parseLong(storeTime);
        long t = System.currentTimeMillis() - st;
        long r = timeUnit.convert(t, TimeUnit.MILLISECONDS);
        return r;
    }


}
