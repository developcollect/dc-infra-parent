package com.developcollect.core.lang.holder;

import java.io.Serializable;
import java.util.Objects;

public abstract class AbstractIdHolder<ID extends Serializable> implements IdHolder<ID> {

    private ID id;

    @Override
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractIdHolder<?> that = (AbstractIdHolder<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "id=" + id;
    }
}
