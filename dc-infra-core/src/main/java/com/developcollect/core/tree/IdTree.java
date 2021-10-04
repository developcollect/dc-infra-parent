package com.developcollect.core.tree;

import com.developcollect.core.lang.holder.IdHolder;

import java.io.Serializable;
import java.util.Optional;

public interface IdTree<T extends IdTree<T, ID>, ID extends Serializable> extends ITree<T>, IdHolder<ID> {

    default ID getParentId() {
        return Optional
                .ofNullable(getParent())
                .map(IdTree::getId)
                .orElse(null);
    }

}
