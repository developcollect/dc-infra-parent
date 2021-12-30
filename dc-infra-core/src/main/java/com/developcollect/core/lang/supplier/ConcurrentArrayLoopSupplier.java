package com.developcollect.core.lang.supplier;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentArrayLoopSupplier<T> implements ResettableSupplier<T> {

    /**
     * 元素数组
     */
    private final Object[] array;
    /**
     * 指向下一个返回的元素
     */
    private final AtomicInteger cursor = new AtomicInteger(0);

    public ConcurrentArrayLoopSupplier(Collection<T> coll) {
        this.array = coll.toArray();
    }

    public ConcurrentArrayLoopSupplier(T[] array) {
        this.array = array;
    }


    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        int idx = cursor.getAndUpdate(prev -> prev + 1 == array.length ? 0 : prev + 1);
        return (T) array[idx];
    }

    @Override
    public void reset() {
        cursor.lazySet(0);
    }
}
