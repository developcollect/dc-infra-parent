package com.developcollect.extra.javacc;

import org.apache.bcel.util.ClassPath;
import org.junit.Test;


public class ListableClassPathRepositoryTest {

    @Test
    public void test2() {
        ListableClassPathRepository repository = new ListableClassPathRepository(ClassPath.SYSTEM_CLASS_PATH);

    }

}