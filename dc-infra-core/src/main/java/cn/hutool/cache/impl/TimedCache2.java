package cn.hutool.cache.impl;

import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.Func1;
import com.developcollect.core.utils.ReflectUtil;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimedCache2<K, V> extends cn.hutool.cache.impl.TimedCache<K, V> {
    public TimedCache2(long timeout) {
        super(timeout);
    }

    public TimedCache2(long timeout, Map<K, CacheObj<K, V>> map) {
        super(timeout, map);
    }

    public V get(K key, long timeout, boolean isUpdateLastAccess, Func0<V> supplier, Func1<V, V> updateFunc) {
        V v = get(key, isUpdateLastAccess);
        if (v == null) {
            if (supplier != null) {
                v = lockAndPutNewValue(key, null, timeout, isUpdateLastAccess, ov -> supplier.call());
            }
        } else if (updateFunc != null) {
            v = lockAndPutNewValue(key, v, timeout, isUpdateLastAccess, updateFunc);
        }
        return v;
    }


    /**
     * 生成新的值并put进缓存
     *
     * @param key                key
     * @param v                  v
     * @param isUpdateLastAccess 是否更新
     * @param processor          值处理器
     * @return v
     */
    private V lockAndPutNewValue(K key, V v, long timeout, boolean isUpdateLastAccess, Func1<V, V> processor) {
        //每个key单独获取一把锁，降低锁的粒度提高并发能力，see pr#1385@Github
        final Lock keyLock = keyLockMap.computeIfAbsent(key, k -> new ReentrantLock());
        keyLock.lock();
        try {
            // 双重检查锁，防止在竞争锁的过程中已经有其它线程写入
            final CacheObj<K, V> co = cacheMap.get(key);
            if (null == co || co.isExpired()) {
                v = processor.callWithRuntimeException(v);
                put(key, v, timeout);
            } else {
                v = co.get(isUpdateLastAccess);
            }
            return v;
        } finally {
            keyLock.unlock();
            keyLockMap.remove(key);
        }
    }

    public Long getLastAccess(K key) {
        CacheObj<K, V> cacheObj = cacheMap.get(key);
        if (cacheObj == null) {
            return null;
        }

        return (Long) ReflectUtil.getFieldValue(cacheObj, "lastAccess");
    }
}
