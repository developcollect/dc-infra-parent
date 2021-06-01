package com.developcollect.core.tree;

import lombok.Data;

import java.util.List;

@Data
public class SimpleMasterNode<T> implements IMasterNode<SimpleMasterNode<T>> {

    private T payload;
    private SimpleMasterNode<T> parent;
    private List<SimpleMasterNode<T>> children;


}
