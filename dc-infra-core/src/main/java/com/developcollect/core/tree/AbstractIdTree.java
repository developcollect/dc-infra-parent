package com.developcollect.core.tree;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public abstract class AbstractIdTree<T extends AbstractIdTree<T, ID>, ID extends Serializable> implements IIdTree<T, ID>  {

    protected ID id;
    protected T parent;
    protected List<T> children;



}
