package com.developcollect.extra.maven;

import com.developcollect.core.tree.IMasterNode;
import lombok.Data;

import java.util.List;


@Data
public class ProjectStructure implements IMasterNode<ProjectStructure> {


    private String groupId;
    private String artifactId;
    private String version;

    private String packaging;

    private String projectPath;
    private String pomPath;


    private ProjectStructure parent;
    private List<ProjectStructure> modules;

    @Override
    public void setChildren(List<ProjectStructure> children) {
        this.modules = children;
    }

    @Override
    public List<ProjectStructure> getChildren() {
        return modules;
    }
}
