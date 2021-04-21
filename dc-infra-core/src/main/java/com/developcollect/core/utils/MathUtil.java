package com.developcollect.core.utils;

import cn.hutool.core.lang.Assert;


/**
 * BigDecimal 工具类
 */
public class MathUtil extends cn.hutool.core.math.MathUtil {

    public static final int SCALE_LEN_COMMON = 2;


    /**
     * 进制转换表
     */
    public static final String RADIX_TABLE = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!$";

    /**
     * 把10进制的数转换成指定进制
     *
     * @param numStr 数字
     * @param radix  目标进制
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/11/6 16:51
     */
    public static String convertRadix(String numStr, int radix) {
        return convertRadix(numStr, 10, radix);
    }

    /**
     * 把任意进制的数字使用默认的进制表转换成另外一种进制
     * 注意：
     * 原始数字需要和进制表相匹配，否则解析出来的数据会错误甚至抛出异常
     *
     * @param numStr 数字
     * @param radix1 原始进制
     * @param radix2 目标进制
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/11/6 16:46
     */
    public static String convertRadix(String numStr, int radix1, int radix2) {
        return convertRadix(numStr, radix1, radix2, RADIX_TABLE);
    }

    /**
     * 把任意进制的数字使用指定的进制表转换成另外一种进制
     * 只支持整数，不支持小数
     * <p>
     * 注意：
     * 原始数字需要和进制表相匹配，否则解析出来的数据会错误甚至抛出异常
     *
     * @param numStr     数字
     * @param radix1     原始进制
     * @param radix2     目标进制
     * @param radixTable 进制表, 进制表中尽量不要使用 '+'、'-'这两个字符，因为这是数字中的正负号，
     *                   如果使用了这两个字符，那么会导致带有正负号的数识别错误
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/11/6 16:46
     */
    public static String convertRadix(String numStr, int radix1, int radix2, String radixTable) {
        Assert.notNull(radixTable);
        if (radixTable.length() < radix2) {
            throw new IllegalArgumentException("进制表不符合" + radix2 + "进制");
        }
        if (radix1 == radix2) {
            return numStr;
        }

        boolean negative = false;
        char[] c1 = numStr.toCharArray();
        int i = 0;
        if (radixTable.indexOf(c1[0]) == -1) {
            if (c1[0] == '-') {
                negative = true;
            } else if (c1[0] != '+') {
                throw new NumberFormatException("For input string: \"" + numStr + "\"");
            }
            if (c1.length == 1) {
                throw new NumberFormatException("For input string: \"" + numStr + "\"");
            }
            i = 1;
        }


        StringBuilder sb = new StringBuilder();
        long t, num = 0;
        for (; i < c1.length; i++) {
            t = radixTable.indexOf(c1[i]);
            if (t < 0) {
                throw new NumberFormatException("For input string: \"" + numStr + "\"");
            }
            num = num * radix1 + t;
        }
        do {
            t = num % radix2;
            sb.append(radixTable.charAt((int) t));
            num /= radix2;
        } while (num != 0);
        if (negative) {
            sb.append("-");
        }

        return sb.reverse().toString();
    }

}
