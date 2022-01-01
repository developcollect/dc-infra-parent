package com.developcollect.core.lang.supplier;

import com.developcollect.core.lang.weight.WeightObj;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机按权重
 */
public class WeightRandomSupplier<T> extends BaseWeightSupplier<T> {

    protected Random random = ThreadLocalRandom.current();

    public WeightRandomSupplier() {
    }

    public WeightRandomSupplier(Collection<WeightObj<T>> weightObjs) {
        super(weightObjs);
    }

    public WeightRandomSupplier<T> setRandom(Random random) {
        this.random = random;
        return this;
    }


    @Override
    public T get() {
        // 随机取一个
        return Optional
                .of(weightMap)
                .map(map -> map.ceilingEntry(random.nextDouble() * totalWeight))
                .map(Map.Entry::getValue)
                .map(WeightObj::getObj)
                .orElse(null);
    }


}
