package com.developcollect.core.geo;

import lombok.Data;

/**
 * 位置
 */
@Data
public class Location {

    /**
     * 国家
     */
    private String nation;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区/县
     */
    private String district;
    /**
     * 编码
     */
    private Integer adcode;
    /**
     * 坐标点
     */
    private Point point;

}
