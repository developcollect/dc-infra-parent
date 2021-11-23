package com.developcollect.core.geo;

import com.developcollect.core.geo.tencent.TencentGeoDecodeResult;
import com.developcollect.core.geo.tencent.TencentLocationUtil;
import org.junit.Test;

public class TencentLocationUtilTest {

    @Test
    public void test_geocode() {
        TencentGeoDecodeResult tencentGeoDecodeResult = TencentLocationUtil.geoDecode(Point.of(116.307490, 39.984154));
        System.out.println(tencentGeoDecodeResult);
    }

}