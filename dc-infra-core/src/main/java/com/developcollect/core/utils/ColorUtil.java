package com.developcollect.core.utils;

import java.awt.*;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/22 14:35
 */
public class ColorUtil {

    /**
     * 字符串转颜色
     * 支持：
     * #CCFFSS
     * CCFFSS
     * #CFS  重复字母简写
     * CFS
     */
    public static Color parseColor(String str) {
        if (str.length() == 7) {
            return hexToColor(str.substring(1));
        } else if (str.length() == 6) {
            return hexToColor(str);
        } else if (str.length() == 4) {
            return shortHexToColor(str.substring(1));
        } else if (str.length() == 3) {
            return shortHexToColor(str);
        }

        throw new IllegalArgumentException("无法解析颜色：" + str);
    }

    /**
     * 16进制转颜色
     * 支持格式：
     * FFE465
     *
     * @param hexStr 16进制文字
     * @return 颜色
     */
    public static Color hexToColor(String hexStr) {
        return new Color(
                Integer.parseInt(hexStr.substring(0, 2), 16),
                Integer.parseInt(hexStr.substring(2, 4), 16),
                Integer.parseInt(hexStr.substring(4, 6), 16)
        );
    }

    /**
     * 简写的16进制转颜色
     * 支持格式：
     * FE5  == FFEE55
     *
     * @param shortHexStr 简写的16进制文字
     * @return 颜色
     */
    public static Color shortHexToColor(String shortHexStr) {
        char c0 = shortHexStr.charAt(0);
        char c1 = shortHexStr.charAt(1);
        char c2 = shortHexStr.charAt(2);
        return hexToColor("" + c0 + c0 + c1 + c1 + c2 + c2);
    }


    public static boolean isColor(String str) {
        try {
            parseColor(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}