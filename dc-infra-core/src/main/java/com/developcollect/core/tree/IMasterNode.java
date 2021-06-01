package com.developcollect.core.tree;


import java.util.List;

/**
 * 主干节点，即含有子节点的节点
 */
public interface IMasterNode<T extends IMasterNode<T>> {

    void setChildren(List<T> children);

    List<T> getChildren();

}
