package com.developcollect.core.lang.supplier;

import com.developcollect.core.lang.weight.WeightObj;
import com.developcollect.core.utils.CollUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机按权重
 */
public class WeightRandomSupplier<T> implements ElementsSupplier<T> {

    protected TreeMap<Double, WeightObj<T>> weightMap = new TreeMap<>();
    /**
     * 总权重
     */
    protected double totalWeight;
    protected Random random = ThreadLocalRandom.current();

    public WeightRandomSupplier() {
    }

    public WeightRandomSupplier(Collection<WeightObj<T>> weightObjs) {
        weightObjs.forEach(this::addElement);
    }

    public WeightRandomSupplier<T> addElement(double weight, T obj) {
        return addElement(WeightObj.of(weight, obj));
    }


    public WeightRandomSupplier<T> addElement(WeightObj<T> weightObj) {
        //以权重区间段的后面的值作为key存当前信息
        totalWeight += weightObj.getWeight();
        weightMap.put(totalWeight, weightObj);
        return this;
    }

    public WeightRandomSupplier<T> setRandom(Random random) {
        this.random = random;
        return this;
    }

    public Collection<WeightObj<T>> weightObjs() {
        return weightMap.values();
    }

    @Override
    public List<T> elements() {
        return CollUtil.convert(weightObjs(), WeightObj::getObj);
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
