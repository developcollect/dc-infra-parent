package com.developcollect.extra.maven;

import lombok.Data;

import java.util.List;


@Data
public class ProjectStructure {


    private String groupId;
    private String artifactId;
    private String version;

    private String packaging;

    private String projectPath;
    private String pomPath;


    private ProjectStructure parent;
    private List<ProjectStructure> modules;
}
