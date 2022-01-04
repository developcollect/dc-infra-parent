package com.developcollect.core.lang.supplier;


import com.developcollect.core.lang.weight.WeightObj;

import java.util.Collection;

/**
 * 按权重提供元素
 *
 */
public interface WeightSupplier<T> extends ElementsSupplier<T> {

    /**
     * 权重元素
     */
    Collection<WeightObj<T>> weightObjs();
}
