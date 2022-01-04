package com.developcollect.core.lang.supplier;


import com.developcollect.core.lang.weight.WeightObj;

import java.util.Collection;

/**
 * 按权重提供元素
 */
public interface WeightSupplier<T> extends ElementsSupplier<T> {

    default WeightSupplier<T> addElement(double weight, T obj) {
        return addElement(WeightObj.of(weight, obj));
    }

    WeightSupplier<T> addElement(WeightObj<T> weightObj);

    /**
     * 权重元素
     */
    Collection<WeightObj<T>> weightObjs();
}
