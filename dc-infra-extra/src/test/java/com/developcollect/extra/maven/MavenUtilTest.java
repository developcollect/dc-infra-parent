package com.developcollect.extra.maven;

import org.junit.Test;

import java.util.List;

public class MavenUtilTest {


    @Test
    public void test_analysisProject() {
        ProjectStructure ps = MavenUtil.analysisProject("/Volumes/D2/code/java-projects/dc-infra-parent");
        System.out.println(ps);
    }


    @Test
    public void test_getDependClassPaths() {
        List<String> compileClassPaths = MavenUtil.getDependClassPaths("/Volumes/D2/code/java-projects/dc-infra-parent");
        System.out.println(compileClassPaths);
    }

    @Test
    public void test_findMavenHome() {
        System.out.println(MavenUtil.findMavenHome());
    }


    @Test
    public void test_error() {
        MavenUtil.mvnWithThrow("/Volumes/D2/code/java-projects/dc-infra-parent", "cclean", "compile");
    }


    @Test
    public void test_clean() {
        MavenUtil.mvn("/Volumes/D2/code/java-projects/dc-infra-parent", "clean");
    }


    @Test
    public void test_package() {
        MavenUtil.mvn("/Volumes/D2/code/java-projects/first", "package");
    }
}