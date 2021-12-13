package com.developcollect.cache;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 */
public class CacheUtil {

    private static Cache<String, String> cache = new MapCache<>();

    public static void set(String key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("暂不支持[null]的Value");
        }
        if (value instanceof String) {
            set(key, (String) value);
        } else {
            // todo value的类型(泛型)选中cache
            throw new IllegalArgumentException("暂不支持[" + value.getClass().getName() + "]类型的Value");
        }
    }

    public static void set(String key, String value) {
        cache.set(key, value);
    }

    public static void set(String key, String value, long timeout, TimeUnit timeUnit) {
        cache.set(key, value, timeout, timeUnit);
    }

    public static void set(String key, String value, Duration duration) {
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


    /**
     * 删除指定key的缓存
     * 无论key是否存在，只要删除没有出错则返回true
     *
     * @param key 缓存的key
     * @return 是否删除完成
     */
    public static boolean delete(String key) {
        return cache.del(key);
    }


    /**
     * 自增指定的key
     * 如果key不存在，则变成1
     */
    public static int incr(String key) {
        return cache.incr(key);
    }

    public static int incr(String key, long timeout, TimeUnit timeUnit) {
        return cache.incr(key, timeout, timeUnit);
    }
}
