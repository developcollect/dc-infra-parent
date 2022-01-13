package com.developcollect.extra.maven;

import com.developcollect.core.tree.ITree;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class DependencyTree implements ITree<DependencyTree> {

    private Dependency dependency;

    private DependencyTree parent;

    private List<DependencyTree> children;



    @Override
    public String toString() {
        return "DependencyTree(dependency=" + this.getDependency()
                + ", parent=" + Optional.ofNullable(this.getParent()).map(DependencyTree::getDependency).orElse(null)
                + ", children=" + Optional.ofNullable(this.getChildren()).map(List::size).orElse(0) + ")";
    }
}
