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
     * 【手机号码】前三位，后4位，其他隐藏，比如135****2210
     *
     * @param num 移动电话；
     * @return 脱敏后的移动电话；
     */
    public static String mobilePhone(String num) {
        return mobilePhone(num, 3, 4);
    }

    public static String mobilePhone(String num, int front, int end) {
        return desensitized(num, front, end);
    }

    /**
     * 通用的脱敏方法
     * @param str 字符串
     * @param front     保留：前面的front位数；从1开始
     * @param end       保留：后面的end位数；从1开始
     */
    public static String desensitized(String str, int front, int end) {
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
        return StrUtil.hide(str, front, str.length() - end);
    }
}
