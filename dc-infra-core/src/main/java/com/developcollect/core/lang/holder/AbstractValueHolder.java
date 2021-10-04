package com.developcollect.core.lang.holder;

public abstract class AbstractValueHolder<T> implements ValueHolder<T> {

    private T value;

    @Override
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
