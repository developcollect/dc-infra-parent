package com.developcollect.core.lang.supplier;


import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 循环从list中返回元素，无穷无尽
 */
public class LoopSupplier<T> implements Supplier<T> {

    /**
     * 元素数组
     */
    private final Object[] array;
    /**
     * 指向下一个返回的元素
     */
    private int cursor;

    public LoopSupplier(Collection<T> coll) {
        this.array = coll.toArray();
    }

    public LoopSupplier(T[] array) {
        this.array = array;
    }

    @Override
    public T get() {
        int idx = cursor++;
        if (cursor == array.length) {
            cursor = 0;
        }
        return (T) array[idx];
    }

}