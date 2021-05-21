package com.developcollect.core.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 坐标点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point {

    /**
     * 经度
     */
    private double lng;
    /**
     * 纬度
     */
    private double lat;

    public static Point of(double lng, double lat) {
        return new Point(lng, lat);
    }

    public static Point of(int lng, int lat) {
        return of((double) lng, lat);
    }

    /**
     * 解析经纬度，115.332,28.3455
     * 前面是经度(lng)，后面是纬度(lat)
     * @param str 经纬度
     * @return point
     */
    public static Point of(String str) {
        String[] split = str.split(", *");
        if (split.length != 2) {
            throw new IllegalArgumentException("无法解析坐标: " + str);
        }
        return of(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    }
}
