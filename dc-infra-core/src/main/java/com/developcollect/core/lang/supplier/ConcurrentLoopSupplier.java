package com.developcollect.core.lang.supplier;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ConcurrentLoopSupplier<T> implements Supplier<T> {

    /**
     * 元素数组
     */
    private final Object[] array;
    /**
     * 指向下一个返回的元素
     */
    private final AtomicInteger cursor = new AtomicInteger(0);

    public ConcurrentLoopSupplier(Collection<T> coll) {
        this.array = coll.toArray();
    }

    public ConcurrentLoopSupplier(T[] array) {
        this.array = array;
    }


    @Override
    public T get() {
        int idx = cursor.getAndUpdate(prev -> prev + 1 == array.length ? 0 : prev + 1);
        return (T) array[idx];
    }

}
