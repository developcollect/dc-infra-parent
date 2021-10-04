package com.developcollect.extra.maven;

import org.junit.Test;

import static org.junit.Assert.*;

public class MavenUtilTest {


    @Test
    public void test_analysisProject() {
        ProjectStructure ps = MavenUtil.analysisProject("/Volumes/D2/code/java-projects/dc-infra-parent");
        System.out.println(ps);
    }

}