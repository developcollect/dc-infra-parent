package com.developcollect.core.lang.supplier;

import com.developcollect.core.lang.weight.WeightObj;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;


public class WeightLoopSupplier<T> extends BaseWeightSupplier<T> implements LoopSupplier<T> {

    /**
     * 指向上一个已返回的元素
     */
    private double cursor = -1;
    /**
     * 遍历的轮数
     */
    private long rounds = 1;

    @Setter
    private long maxRounds = Long.MAX_VALUE;


    @Override
    public T get() {
        ++cursor;
        if (cursor > totalWeight) {
            cursor = 0;
            if (++rounds > maxRounds) {
                throw new RoundOutOfBoundsException(rounds, maxRounds);
            }
        }
        // 顺序取一个
        return Optional
                .of(weightMap)
                .map(map -> map.ceilingEntry(cursor))
                .map(Map.Entry::getValue)
                .map(WeightObj::getObj)
                .orElse(null);
    }

    @Override
    public long rounds() {
        return rounds;
    }


    @Override
    public boolean hasNext() {
        if (rounds < maxRounds) {
            return true;
        }
        if (rounds == maxRounds && cursor + 1 <= totalWeight) {
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        cursor = -1;
        rounds = 1;
    }

}