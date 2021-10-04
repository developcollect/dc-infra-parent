package com.developcollect.core.lang.holder;

public abstract class AbstractParentHolder<T> implements ParentHold<T> {

    private T parent;

    @Override
    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }
}
