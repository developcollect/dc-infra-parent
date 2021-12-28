package com.developcollect.extra.maven;

import lombok.Data;

@Data
public class Artifact {

    private String groupId;
    private String artifactId;
    private String version;


    @Override
    public String toString() {
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
    }

}
