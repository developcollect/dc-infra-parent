package com.developcollect.extra.javacc;

import com.developcollect.core.lang.SystemClock;
import org.apache.bcel.generic.Type;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CcUtilTest {



    @Test
    public void test2a() {
        CcUtil.parseChain("/Volumes/D2/code/java-projects/dc-infra-parent");
    }


}