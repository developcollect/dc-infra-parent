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

    private double lat;
    private double lng;

    public static Point of(double lat, double lng) {
        return new Point(lat, lng);
    }

    public static Point of(int lat, int lng) {
        return of((double) lat, (double) lng);
    }
}
