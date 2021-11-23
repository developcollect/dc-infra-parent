package com.developcollect.core.geo.tencent;

import com.alibaba.fastjson.annotation.JSONField;
import com.developcollect.core.geo.Point;
import lombok.Data;

import java.util.List;

@Data
public class TencentGeoDecodeResult {

    @JSONField(name = "location")
    private Point point;

    /**
     * 以行政区划+道路+门牌号等信息组成的标准格式化地址
     */
    private String address;

    /**
     * 结合知名地点形成的描述性地址，更具人性化特点
     */
    @JSONField(name = "formatted_addresses")
    private FormattedAddresses formattedAddresses;

    /**
     * 地址部件，address不满足需求时可自行拼接
     */
    @JSONField(name = "address_component")
    private AddressComponent addressComponent;

    /**
     * 行政区划信息
     */
    @JSONField(name = "ad_info")
    private AdInfo adInfo;


    /**
     * 坐标相对位置参考
     */
    @JSONField(name = "address_reference")
    private AddressReference addressReference;

    /**
     * 查询的周边poi的总数，仅在传入参数get_poi=1时返回
     */
    @JSONField(name = "poi_count")
    private Integer poiCount;

    /**
     * 周边地点（POI）数组，数组中每个子项为一个POI对象
     */
    private List<Poi> pois;




    @Data
    public static class FormattedAddresses {
        /**
         * 推荐使用的地址描述，描述精确性较高
         */
        private String recommend;
        /**
         * 粗略位置描述
         */
        private String rough;
    }

    @Data
    public static class AddressComponent {
        /**
         * 是	国家
         */
        private String nation;
        /**
         * 是	省
         */
        private String province;
        /**
         * 是	市
         */
        private String city;
        /**
         * 否	区，可能为空字串
         */
        private String district;
        /**
         * 否	街道，可能为空字串
         */
        private String street;
        /**
         * 否	门牌，可能为空字串
         */
        @JSONField(name = "street_number")
        private String streetNumber;
    }

    @Data
    public static class AdInfo {

        /**
         * 是	国家代码（ISO3166标准3位数字码）
         */
        @JSONField(name = "nation_code")
        private String nationCode;

        /**
         * 是	行政区划代码，规则详见：行政区划代码说明
         */
        private String adcode;

        /**
         * 是	城市代码，由国家码+行政区划代码（提出城市级别）组合而来，总共为9位
         */
        @JSONField(name = "city_code")
        private String cityCode;

        /**
         * 是	行政区划名称
         */
        private String name;

        /**
         * 是	行政区划中心点坐标
         */
        @JSONField(name = "location")
        private Point point;

        /**
         * 是	国家
         */
        private String nation;

        /**
         * 是	省 / 直辖市
         */
        private String province;

        /**
         * 是	市 / 地级区 及同级行政区划
         */
        private String city;

        /**
         * 否	区 / 县级市 及同级行政区划
         */
        private String district;
    }

    @Data
    public static class Poi {

        /**
         * 否	地点（POI）唯一标识
         */
        private String id;

        /**
         * 否	名称
         */
        private String title;

        /**
         * 否	地址
         */
        private String address;

        /**
         * 否	地点分类信息
         */
        private String category;

        /**
         * 否	提示所述位置坐标
         */
        @JSONField(name = "location")
        private Point point;

        /**
         * 否	行政区划信息
         */
        @JSONField(name = "ad_info")
        private AdInfo adInfo;


        /**
         * 否	该POI到逆地址解析传入的坐标的直线距离
         */
        @JSONField(name = "_distance")
        private String distance;

        /**
         * 否	此参考位置到输入坐标的方位关系，如：北、南、内
         */
        @JSONField(name = "_dir_desc")
        private String dirDesc;
    }


    @Data
    public static class AddressReference {

        /**
         * 知名区域，如商圈或人们普遍认为有较高知名度的区域
         */
        @JSONField(name = "famous_area")
        private Poi famousArea;

        /**
         * 商圈，目前与famous_area一致
         */
        @JSONField(name = "business_area")
        private Poi businessArea;

        /**
         * 乡镇街道
         */
        private Poi town;


        /**
         * 否	一级地标，可识别性较强、规模较大的地点、小区等
         */
        @JSONField(name = "landmark_l1")
        private Poi landmarkL1;

        /**
         * 否	二级地标，较一级地标更为精确，规模更小
         */
        @JSONField(name = "landmark_l2")
        private Poi landmarkL2;

        /**
         * 否	街道
         */
        private Poi street;
        /**
         * 否	门牌
         */
        @JSONField(name = "street_number")
        private Poi streetNumber;
        /**
         * 否	交叉路口
         */
        private Poi crossroad;
        /**
         * 否	水系
         */
        private Poi water;
    }
}
