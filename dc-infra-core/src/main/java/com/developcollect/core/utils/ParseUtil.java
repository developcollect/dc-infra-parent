package com.developcollect.core.utils;


import java.awt.*;

/**
 * 解析数据工具类
 */
public class ParseUtil {

    public static int parseInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean parseBoolean(String str, boolean defaultValue) {
        if (Boolean.TRUE.toString().equalsIgnoreCase(str)) {
            return true;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(str)) {
            return false;
        } else {
            return defaultValue;
        }
    }

    public static Color parseColor(String str, Color defaultValue) {
        try {
            return ColorUtil.parseColor(str);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
