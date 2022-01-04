package com.developcollect.core.lang.supplier;

import com.developcollect.core.lang.weight.WeightObj;
import com.developcollect.core.utils.CollUtil;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

public abstract class BaseWeightSupplier<T> implements WeightSupplier<T> {
    protected TreeMap<Double, WeightObj<T>> weightMap = new TreeMap<>();
    /**
     * 总权重
     */
    protected double totalWeight;

    public BaseWeightSupplier() {
    }

    public BaseWeightSupplier(Collection<WeightObj<T>> weightObjs) {
        weightObjs.forEach(this::addElement);
    }

    @Override
    public BaseWeightSupplier<T> addElement(WeightObj<T> weightObj) {
        //以权重区间段的后面的值作为key存当前信息
        totalWeight += weightObj.getWeight();
        weightMap.put(totalWeight, weightObj);
        return this;
    }

    public List<T> elements() {
        return CollUtil.convert(weightObjs(), WeightObj::getObj);
    }

    @Override
    public Collection<WeightObj<T>> weightObjs() {
        return weightMap.values();
    }


}
