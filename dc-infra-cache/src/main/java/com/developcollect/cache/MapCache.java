package com.developcollect.cache;

import cn.hutool.cache.impl.TimedCache2;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MapCache<K, V> implements Cache<K, V> {
    /**
     * TimedCache里有线程安全处理(具体没仔细看，只看到了有lock)，所以这里不做处理
     */
    private TimedCache2<K, V> cache = new TimedCache2<>(0, new HashMap<>());


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
        return cache.get(key, false);
    }

    @Override
    public boolean del(K key) {
        cache.remove(key);
        return true;
    }

    @Override
    public void setAndRecordTime(K key, V value) {
        set(key, value);
    }

    @Override
    public void setAndRecordTime(K key, V value, long timeout, TimeUnit timeUnit) {
        set(key, value, timeout, timeUnit);
    }

    @Override
    public long getAge(K key, TimeUnit timeUnit) {
        Long storeTime = cache.getLastAccess(key);
        if (storeTime == null) {
            return -1;
        }
        long t = System.currentTimeMillis() - storeTime;
        long r = timeUnit.convert(t, TimeUnit.MILLISECONDS);
        return r;
    }

    @Override
    public int incr(K key) {
        V value = cache.get(key, 0, false, () -> (V) (Object) 0, v -> (V) (Object) (((Integer) v) + 1));
        return (Integer) value;
    }

    @Override
    public int incr(K key, long timeout, TimeUnit timeUnit) {
        V value = cache.get(key, timeUnit.toMillis(timeout), false, () -> (V) (Object) 0, v -> (V) (Object) (((Integer) v) + 1));
        return (Integer) value;
    }
}
