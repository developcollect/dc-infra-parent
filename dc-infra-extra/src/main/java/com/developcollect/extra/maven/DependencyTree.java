package com.developcollect.extra.maven;

import com.developcollect.core.tree.ITree;
import lombok.Data;

import java.util.List;

@Data
public class DependencyTree implements ITree<DependencyTree> {

    private Dependency dependency;

    private DependencyTree parent;

    private List<DependencyTree> children;


}
