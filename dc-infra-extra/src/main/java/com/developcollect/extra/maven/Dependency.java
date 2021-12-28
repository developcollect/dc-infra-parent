package com.developcollect.extra.maven;

import lombok.Data;

@Data
public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
    private String type;

    @Override
    public String toString() {
        return getGroupId() + ":" + getArtifactId() + ":" + getType() + ":" + getVersion() + ":" + getScope();
    }
}
