package com.developcollect.cache;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class StringRedisTemplateCache implements Cache<String, String> {

    private static final String RECORD_TIME_KEY_PREFIX = "REDIS_KEY_STORE_TIME:";

    private StringRedisTemplate redisTemplate;

    public StringRedisTemplateCache(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public void setAndRecordTime(String key, String value) {
        redisTemplate.opsForValue().multiSet(new HashMap<String, String>() {{
            put(key, value);
            put(RECORD_TIME_KEY_PREFIX + key, Long.toString(System.currentTimeMillis()));
        }});
    }


    @Override
    public void setAndRecordTime(String key, String value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        redisTemplate.opsForValue().set(RECORD_TIME_KEY_PREFIX + key, Long.toString(System.currentTimeMillis()), timeout, timeUnit);
    }

    @Override
    public long getAge(String key, TimeUnit timeUnit) {
        String storeTime = redisTemplate.opsForValue().get(RECORD_TIME_KEY_PREFIX + key);
        if (storeTime == null) {
            return -1;
        }
        long st = Long.parseLong(storeTime);
        long age = timeUnit.convert(System.currentTimeMillis() - st, TimeUnit.MILLISECONDS);
        return age;
    }

}
