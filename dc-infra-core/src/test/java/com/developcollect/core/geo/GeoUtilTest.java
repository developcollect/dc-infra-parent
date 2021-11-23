package com.developcollect.core.geo;

import com.developcollect.core.utils.NetUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeoUtilTest {

    @Test
    public void test() {
        String internetIp = NetUtil.getInternetIp();
        Location location = GeoUtil.getIpLocation(internetIp);
        String addressByPoint = GeoUtil.getAddressByPoint(location.getPoint());
        System.out.println(addressByPoint);
    }
}