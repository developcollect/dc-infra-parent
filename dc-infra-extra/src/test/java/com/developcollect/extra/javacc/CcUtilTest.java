package com.developcollect.extra.javacc;

import org.junit.Test;

public class CcUtilTest {



    @Test
    public void test2a() {
        CcUtil.parseChain("/Volumes/D2/code/java-projects/dc-infra-parent", javaClass -> true);
    }


}