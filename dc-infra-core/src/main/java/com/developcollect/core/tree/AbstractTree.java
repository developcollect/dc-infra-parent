package com.developcollect.core.tree;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public abstract class AbstractTree<T extends AbstractTree<T>> implements ITree<T> {

    protected T parent;
    protected List<T> children;

}
