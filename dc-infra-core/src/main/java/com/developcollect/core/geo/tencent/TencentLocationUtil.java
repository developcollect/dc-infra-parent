package com.developcollect.core.geo.tencent;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.developcollect.core.geo.Location;
import com.developcollect.core.geo.Point;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于腾讯位置服务实现的定位相关工具
 * https://lbs.qq.com/service/webService/webServiceGuide/webServiceGcoder
 */
@Slf4j
public class TencentLocationUtil {


    /**
     * 根据ip地址获取定位信息
     *
     * @param ip
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/4/30 10:23
     */
    public static Location getIpLocation(String ip) {
        try {
            String body = HttpRequest
                    .get(String.format("https://apis.map.qq.com/ws/location/v1/ip?ip=%s&key=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77", ip))
                    .header("Referer", "https://lbs.qq.com/service/webService/webServiceGuide/webServiceIp")
                    .execute()
                    .body();
            final JSONObject jo = JSONObject.parseObject(body);
            if (jo.getIntValue("status") == 0) {
                Location location = new Location();
                final JSONObject result = jo.getJSONObject("result");
                final JSONObject adInfo = result.getJSONObject("ad_info");
                final JSONObject pointInfo = result.getJSONObject("location");
                location.setNation(adInfo.getString("nation"));
                location.setProvince(adInfo.getString("province"));
                location.setCity(adInfo.getString("city"));
                location.setDistrict(adInfo.getString("district"));
                location.setAdcode(adInfo.getInteger("adcode"));
                if (pointInfo != null) {
                    Point point = new Point();
                    location.setPoint(point);
                    point.setLat(pointInfo.getDouble("lat"));
                    point.setLng(pointInfo.getDouble("lng"));
                }

                return location;
            } else {
                log.error("调用IP定位接口失败, IP:[{}], Response:[{}]", ip, body);
            }
        } catch (Exception e) {
            log.error("获取IP定位失败, IP:[{}]", ip, e);
        }
        return null;
    }


    /**
     * 逆地理编码
     * 根据经纬度获取文字地址及相关位置信息
     */
    public static TencentGeoDecodeResult geoDecode(Point point) {
        try {
            String body = HttpRequest
                    .get(String.format("https://apis.map.qq.com/ws/geocoder/v1/?location=%s,%s&key=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77&get_poi=1", point.getLat(), point.getLng()))
                    .header("Referer", "https://lbs.qq.com/service/webService/webServiceGuide/webServiceGcoder")
                    .execute()
                    .body();
            final JSONObject jo = JSONObject.parseObject(body);
            if (jo.getIntValue("status") == 0) {
                JSONObject resultJo = jo.getJSONObject("result");
                TencentGeoDecodeResult tencentGeoDecodeResult = resultJo.toJavaObject(TencentGeoDecodeResult.class);
                return tencentGeoDecodeResult;
            } else {
                log.error("调用逆地理编码接口失败, point:[{}], Response:[{}]", point, body);
            }
        } catch (Exception e) {
            log.error("调用逆地理编码接口失败, point:[{}]", point, e);
        }
        return null;
    }

}
