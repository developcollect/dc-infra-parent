package com.developcollect.core.lang.supplier;

public interface LoopSupplier<T> extends ResettableSupplier<T> {

    /**
     * 游标是否在头节点位置
     * @return 如果在头结点返回true
     */
    boolean atHead();

}
