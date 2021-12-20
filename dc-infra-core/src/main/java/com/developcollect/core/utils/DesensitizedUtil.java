package com.developcollect.core.utils;


public class DesensitizedUtil extends cn.hutool.core.util.DesensitizedUtil {

    /**
     * 【身份证号】前1位 和后2位
     *
     * @param idCardNum 身份证
     * @return 脱敏后的身份证
     */
    public static String idCardNum(String idCardNum) {
        return idCardNum(idCardNum, 1, 2);
    }

    /**
     * 【手机号码】前2位，后3位，其他隐藏，比如13******210
     *
     * @param num 移动电话；
     * @return 脱敏后的移动电话；
     */
    public static String mobilePhone(String num) {
        return mobilePhone(num, 2, 3);
    }

    public static String mobilePhone(String num, int front, int end) {
        return desensitized(num, front, end);
    }


    /**
     * [公司开户银行联号] 公司开户银行联行号,显示前两位，其他用星号隐藏，每位1个星号<例子:12********>
     */
    public static String cnapsCode(final String code) {
        if (StrUtil.isBlank(code)) {
            return "";
        }
        return desensitized(code, 2, 0, -1);
    }

    /**
     * 【密码】直接返回 ********
     *
     * @param password 密码
     * @return 脱敏后的密码
     */
    public static String password(String password) {
        return "********";
    }

    /**
     * 通用的脱敏方法
     *
     * @param str   字符串
     * @param front 保留：前面的front位数；从1开始
     * @param end   保留：后面的end位数；从1开始
     */
    public static String desensitized(String str, int front, int end) {
        return desensitized(str, front, end, -1);
    }

    /**
     * 通用的脱敏方法
     * @param str 字符串
     * @param front     保留：前面的front位数；从1开始
     * @param end       保留：后面的end位数；从1开始
     * @param stars     保留多少个*  小于0的话就是自动
     */
    public static String desensitized(String str, int front, int end, int stars) {
        // 字符串不能为空
        if (StrUtil.isBlank(str)) {
            return StrUtil.EMPTY;
        }
        // 需要截取的长度不能大于字符串长度
        if ((front + end) > str.length()) {
            return StrUtil.EMPTY;
        }
        // 需要截取的不能小于0
        if (front < 0 || end < 0) {
            return StrUtil.EMPTY;
        }

        return StrUtil.hide(str, front, str.length() - end, stars);
    }
}
