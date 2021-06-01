package com.developcollect.core.tree;

import java.io.Serializable;
import java.util.Optional;

public interface IIdTree<T extends IIdTree<T, ID>, ID extends Serializable> extends ITree<T> {

    ID getId();

    default ID getParentId() {
        return Optional
                .ofNullable(getParent())
                .map(IIdTree::getId)
                .orElse(null);
    }

    T getParent();
}
