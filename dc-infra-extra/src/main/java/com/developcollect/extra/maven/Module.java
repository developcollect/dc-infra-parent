package com.developcollect.extra.maven;

import lombok.Data;

import java.util.List;


@Data
public class Module {

    /**
     * 模块信息
     */
    private Artifact artifact;

    /**
     * 依赖列表
     */
    private List<Dependency> dependencies;

    /**
     * 树状的依赖列表
     */
    private List<DependencyTree> dependencyTrees;
}
