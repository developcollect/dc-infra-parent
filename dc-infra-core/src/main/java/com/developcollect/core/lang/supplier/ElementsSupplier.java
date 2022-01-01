package com.developcollect.core.lang.supplier;

import java.util.List;
import java.util.function.Supplier;

/**
 * 以基础的元素集合为基础，提供元素
 *
 * @param <T>
 */
public interface ElementsSupplier<T> extends Supplier<T> {

    /**
     * 基础元素集合
     */
    <C extends List<T>> C elements();

}
