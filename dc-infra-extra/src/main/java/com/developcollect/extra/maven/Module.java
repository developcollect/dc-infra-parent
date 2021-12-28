package com.developcollect.extra.maven;

import lombok.Data;

import java.util.List;


@Data
public class Module {

    private Artifact artifact;
    private List<Dependency> dependencies;

}
