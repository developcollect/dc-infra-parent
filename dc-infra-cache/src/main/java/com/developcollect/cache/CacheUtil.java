package com.developcollect.cache;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 */
public class CacheUtil {

    private static Cache<String, String> cache = new MapCache<>();


    public static void set(String key, String value) {
        cache.set(key, value);
    }

    public static void set(String key, String value, long timeout, TimeUnit timeUnit) {
        cache.set(key, value, timeout, timeUnit);
    }

    public static void  set(String key, String value, Duration duration) {
        cache.set(key, value, duration);
    }


    public static String get(String key) {
        return cache.get(key);
    }


    public static void setAndRecordTime(String key, String value) {
        cache.setAndRecordTime(key, value);
    }

    public static void setAndRecordTime(String key, String value, Duration duration) {
        cache.setAndRecordTime(key, value, duration);
    }

    public static void setAndRecordTime(String key, String value, long timeout, TimeUnit timeUnit) {
        cache.setAndRecordTime(key, value, timeout, timeUnit);
    }

    /**
     * 获取key已存在的时间
     * 使用本方法的key必须是先用{@link #setAndRecordTime}方法保存的值, 不然获取的都是-1
     *
     * @param key      key
     * @param timeUnit 时间单位
     * @return long
     */
    public static long getAge(String key, TimeUnit timeUnit) {
        return cache.getAge(key, timeUnit);
    }


    public static long getAge(String key) {
        return cache.getAge(key);
    }
}
