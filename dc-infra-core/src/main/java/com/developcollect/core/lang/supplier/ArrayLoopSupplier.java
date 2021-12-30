package com.developcollect.core.lang.supplier;


import com.developcollect.core.lang.Assert;

import java.util.Collection;

/**
 * 循环从数组中返回元素，无穷无尽
 */
public class ArrayLoopSupplier<T> implements ResettableSupplier<T> {

    /**
     * 元素数组
     */
    private final Object[] array;
    /**
     * 指向下一个返回的元素
     */
    private int cursor;

    public ArrayLoopSupplier(Collection<T> coll) {
        Assert.notEmpty(coll);
        this.array = coll.toArray();
    }

    public ArrayLoopSupplier(T[] array) {
        Assert.notEmpty(array);
        this.array = array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        int idx = cursor++;
        if (cursor == array.length) {
            cursor = 0;
        }
        return (T) array[idx];
    }

    @Override
    public void reset() {
        cursor = 0;
    }
}