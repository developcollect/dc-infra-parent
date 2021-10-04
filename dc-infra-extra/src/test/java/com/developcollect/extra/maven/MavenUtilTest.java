package com.developcollect.extra.maven;

import org.junit.Test;

public class MavenUtilTest {


    @Test
    public void test_analysisProject() {
        ProjectStructure ps = MavenUtil.analysisProject("/Volumes/D2/code/java-projects/dc-infra-parent");
        System.out.println(ps);
    }


    @Test
    public void test_findMavenHome() {
        System.out.println(MavenUtil.findMavenHome());
    }


    @Test
    public void test_error() {
        MavenUtil.mvnWithThrow("/Volumes/D2/code/java-projects/dc-infra-parent", "haahah");
    }
}