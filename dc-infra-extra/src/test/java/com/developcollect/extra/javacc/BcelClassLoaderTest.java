package com.developcollect.extra.javacc;

import org.apache.bcel.util.ClassPath;
import org.junit.Before;
import org.junit.Test;

public class BcelClassLoaderTest {
    private ListableClassPathRepository repository;


    @Before
    public void init() {

    }


    @Test
    public void test_getImplClass() {
    }


    @Test
    public void ggg() {
        ClassPath classPath = ClassPath.SYSTEM_CLASS_PATH;
        new ClassPath("/Volumes/D2/code/java-projects/first/target/classes");
        System.out.println('s');
    }
}