package com.developcollect.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;


public class SORedisTemplateCache implements Cache<String, Object> {

    private final RedisTemplate<String, Object> redisTemplate;

    public SORedisTemplateCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Object value) {
        // 序列化器
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public boolean del(String key) {
        return false;
    }

    @Override
    public void setAndRecordTime(String key, Object value) {

    }

    @Override
    public void setAndRecordTime(String key, Object value, long timeout, TimeUnit timeUnit) {

    }

    @Override
    public long getAge(String key, TimeUnit timeUnit) {
        return 0;
    }
}
