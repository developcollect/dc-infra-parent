package com.developcollect.core.lang.supplier;

import java.util.LinkedList;
import java.util.List;

/**
 * 包含备用结果的提供器
 * 提供出一个结果后，其他剩余的结果都作为后备结果
 */
public class FallbackChainSupplierWrapper<T> implements ElementsSupplier<List<T>> {

    private final ElementsSupplier<T> delegate;

    public FallbackChainSupplierWrapper(ElementsSupplier<T> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> elements() {
        return delegate.elements();
    }

    @Override
    public List<T> get() {
        T head = delegate.get();
        LinkedList<T> list = new LinkedList<>();
        list.add(head);
        for (T element : delegate.elements()) {
            if (!element.equals(head)) {
                list.add(element);
            }
        }
        return list;
    }
}
