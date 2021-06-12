package com.developcollect.cache;

import cn.hutool.cache.impl.TimedCache;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MapCache<K, V> implements Cache<K, V> {
    private static final String RECORD_TIME_KEY_PREFIX = "CACHE_KEY_STORE_TIME:";
    private TimedCache<K, V> cache = new TimedCache<>(0, new HashMap<>());


    @Override
    public void set(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public void set(K key, V value, long timeout, TimeUnit timeUnit) {
        cache.put(key, value, timeUnit.toMillis(timeout));
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public boolean del(K key) {
        cache.remove(key);
        return true;
    }

    @Override
    public void setAndRecordTime(K key, V value) {
        cache.put(key, value);
        cache.put((K)(RECORD_TIME_KEY_PREFIX + key), (V) (Long) System.currentTimeMillis());
        System.out.println(getAge(key));
    }

    @Override
    public void setAndRecordTime(K key, V value, long timeout, TimeUnit timeUnit) {
        cache.put(key, value, timeUnit.toMillis(timeout));
        cache.put((K)(RECORD_TIME_KEY_PREFIX + key), (V) (Long) System.currentTimeMillis(), timeUnit.toMillis(timeout));
    }

    @Override
    public long getAge(K key, TimeUnit timeUnit) {
        Long storeTime = (Long) cache.get((K) (RECORD_TIME_KEY_PREFIX + key));
        if (storeTime == null) {
            return -1;
        }
        long t = System.currentTimeMillis() - storeTime;
        long r = timeUnit.convert(t, TimeUnit.MILLISECONDS);
        return r;
    }


}
