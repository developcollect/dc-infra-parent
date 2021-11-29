package com.developcollect.extra.javacc;

import com.developcollect.core.utils.FileUtil;
import org.apache.bcel.util.ClassPath;
import org.junit.Test;

import java.io.File;

public class CcSupportTest {


    @Test
    public void test_ss() throws ClassNotFoundException {
        ListableClassPathRepository repository = new ListableClassPathRepository(new ClassPath("/Volumes/D2/code/java-projects/first/target/classes"));
        CcSupport.getImplClassList(repository, "org.example.ITestService");

    }


    @Test
    public void test_fcn() {
        File file = new File(".");
        System.out.println(file.getAbsoluteFile());
        System.out.println(FileUtil.getCanonicalPath(file));
    }

}