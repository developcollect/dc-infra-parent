package com.developcollect.core.geo;

import com.developcollect.core.geo.tencent.TencentGeoDecodeResult;
import com.developcollect.core.geo.tencent.TencentLocationUtil;

import java.util.Optional;

public class GeoUtil {

    /**
     * 根据ip地址获取定位信息
     *
     * @param ip ip
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/4/30 10:23
     */
    public static Location getIpLocation(String ip) {
        return TencentLocationUtil.getIpLocation(ip);
    }

    /**
     * 根据坐标点获取地址信息
     *
     * @param point 坐标点
     * @return 地址
     */
    public static String getAddressByPoint(Point point) {
        TencentGeoDecodeResult tencentGeoDecodeResult = TencentLocationUtil.geoDecode(point);
        return Optional.ofNullable(tencentGeoDecodeResult).map(TencentGeoDecodeResult::getAddress).orElse(null);
    }
}
