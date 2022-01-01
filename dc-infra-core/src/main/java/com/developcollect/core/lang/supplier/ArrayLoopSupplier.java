package com.developcollect.core.lang.supplier;


import com.developcollect.core.lang.Assert;

import java.util.Collection;

/**
 * 循环从数组中返回元素，无穷无尽
 * 线程不安全，不能在多线程环境下使用
 *
 * @author zak
 */
public class ArrayLoopSupplier<T> implements LoopSupplier<T> {

    /**
     * 元素数组
     */
    private final Object[] array;
    /**
     * 指向上一个已返回的元素
     */
    private int cursor = -1;
    /**
     * 遍历的轮数
     */
    private long rounds = 1;
    private long maxRounds = Long.MAX_VALUE;

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
        if (++cursor == array.length) {
            cursor = 0;
            if (++rounds > maxRounds) {
                throw new RoundOutOfBoundsException(rounds, maxRounds);
            }
        }
        return (T) array[cursor];
    }


    /**
     * 重置，一切从头来过
     */
    @Override
    public void reset() {
        cursor = -1;
        rounds = 1;
    }

    @Override
    public long rounds() {
        return rounds;
    }

    @Override
    public void setMaxRounds(long maxRounds) {
        this.maxRounds = maxRounds;
    }

    @Override
    public boolean hasNext() {
        if (rounds < maxRounds) {
            return true;
        }
        if (rounds == maxRounds && cursor + 1 < array.length) {
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] elements() {
        return (T[]) array;
    }
}