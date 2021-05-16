package com.developcollect.core.utils;

import com.developcollect.core.geo.Location;
import org.junit.Test;

import static org.junit.Assert.*;

public class NetUtilTest {


    @Test
    public void test2() {
        Location ipLocation = NetUtil.getIpLocation("117.44.228.134");
        System.out.println(ipLocation);
    }
}