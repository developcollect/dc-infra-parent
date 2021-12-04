package com.developcollect.cache;


import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface Cache<K, V>  {

    void set(K key, V value);

    void set(K key, V value, long timeout, TimeUnit timeUnit);

    default void set(K key, V value, Duration duration) {
        long seconds = duration.getSeconds();
        set(key, value, seconds, TimeUnit.SECONDS);
    }

    V get(K key);

    boolean del(K key);

    /**
     * 设置值并且记录当前时间，用于之后计算key存在的时长
     * @param key 键
     * @param value 值
     */
    void setAndRecordTime(K key, V value);


    default void setAndRecordTime(K key, V value, Duration duration) {
        setAndRecordTime(key, value, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    void setAndRecordTime(K key, V value, long timeout, TimeUnit timeUnit);

    /**
     * 获取key已存在的时间
     * 使用本方法的key必须是先用{@link #setAndRecordTime}方法保存的值, 不然获取的都是-1
     *
     * @param key      key
     * @param timeUnit 时间单位
     * @return long
     */
    long getAge(K key, TimeUnit timeUnit);

    default long getAge(K key) {
        return getAge(key, TimeUnit.SECONDS);
    }


    int incr(K key);

    int incr(K key, long timeout, TimeUnit timeUnit);
}
