package com.developcollect.core.tree;


public class PathTree extends SimpleMasterNode<String> {

    public String getPath() {
        return getPayload();
    }

}
