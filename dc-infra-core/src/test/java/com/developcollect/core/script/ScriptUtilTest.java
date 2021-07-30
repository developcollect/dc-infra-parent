package com.developcollect.core.script;

import com.developcollect.core.io.resource.ResourceUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class ScriptUtilTest {

    @Test
    public void test1() {
        String str = ResourceUtil.readUtf8Str("demo.js");
        System.out.println(str);
    }

}