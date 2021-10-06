package com.developcollect.extra.javacc;

import org.apache.bcel.util.ClassPath;
import org.junit.Test;

public class CcInnerUtilTest {


    @Test
    public void test_ss() throws ClassNotFoundException {
        ListableClassPathRepository repository = new ListableClassPathRepository(new ClassPath("/Volumes/D2/code/java-projects/first/target/classes"));
        CcInnerUtil.getImplClassList(repository, "org.example.ITestService");

    }

}