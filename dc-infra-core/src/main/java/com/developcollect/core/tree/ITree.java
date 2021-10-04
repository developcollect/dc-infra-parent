package com.developcollect.core.tree;

import com.developcollect.core.lang.holder.ParentHold;

public interface ITree<T extends ITree<T>> extends IMasterNode<T>, ParentHold<T> {
}
