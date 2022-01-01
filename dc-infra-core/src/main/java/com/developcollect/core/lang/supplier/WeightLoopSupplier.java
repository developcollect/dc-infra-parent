package com.developcollect.core.lang.supplier;

import lombok.Setter;


public class WeightLoopSupplier<T> extends BaseWeightSupplier<T> implements LoopSupplier<T> {

    /**
     * 指向上一个已返回的元素
     */
    private int cursor = -1;
    /**
     * 遍历的轮数
     */
    private long rounds = 1;

    @Setter
    private long maxRounds = Long.MAX_VALUE;


    @Override
    public T get() {
        return null;
    }

    @Override
    public long rounds() {
        return rounds;
    }


    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void reset() {
        cursor = -1;
        rounds = 1;
    }

}
