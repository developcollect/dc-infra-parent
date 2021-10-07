package com.developcollect.extra.maven;

import com.developcollect.core.tree.IMasterNode;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class ProjectStructure implements IMasterNode<ProjectStructure> {


    private String groupId;
    private String artifactId;
    private String version;

    private String packaging;

    private String projectPath;
    private String pomPath;
    private List<String> modules;


    private ProjectStructure parent;
    private List<ProjectStructure> moduleProjectStructures;

    public ProjectStructure() {
    }


    @Override
    public void setChildren(List<ProjectStructure> children) {
        this.setModuleProjectStructures(moduleProjectStructures);
    }

    @Override
    public List<ProjectStructure> getChildren() {
        return this.getModuleProjectStructures();
    }


    @Override
    public String toString() {
        return "ProjectStructure(groupId=" + this.getGroupId()
                + ", artifactId=" + this.getArtifactId()
                + ", version=" + this.getVersion()
                + ", packaging=" + this.getPackaging()
                + ", projectPath=" + this.getProjectPath()
                + ", pomPath=" + this.getPomPath()
                + ", modules=" + this.getModules()
                + ", parent=" + Optional.ofNullable(this.getParent()).map(ProjectStructure::getProjectPath).orElse(null)
                + ", moduleProjectStructures="
                + this.getModuleProjectStructures()
                + ")";
    }
}
