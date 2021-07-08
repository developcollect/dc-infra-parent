package com.developcollect.core.utils;


import cn.hutool.core.util.ArrayUtil;
import com.developcollect.core.lang.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

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

    public static String convertRadix(String numStr, int radix1, int radix2, String radixTable) {
        return convertRadix(numStr, radix1, radixTable, radix2, radixTable);
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
     * @param radixTable1 radix1使用的进制表, 进制表中尽量不要使用 '+'、'-'这两个字符，因为这是数字中的正负号，
     *                   如果使用了这两个字符，那么会导致带有正负号的数识别错误
     * @param radixTable2 radix2使用的进制表, 进制表中尽量不要使用 '+'、'-'这两个字符，因为这是数字中的正负号，
     *                   如果使用了这两个字符，那么会导致带有正负号的数识别错误
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/11/6 16:46
     */
    public static String convertRadix(String numStr, int radix1, String radixTable1, int radix2, String radixTable2) {
        Assert.notNull(radixTable1);
        Assert.notNull(radixTable2);
        if (radixTable1.length() < radix1) {
            throw new IllegalArgumentException("进制表不符合" + radix1 + "进制");
        }
        if (radixTable2.length() < radix2) {
            throw new IllegalArgumentException("进制表不符合" + radix2 + "进制");
        }
        if (radix1 == radix2) {
            return numStr;
        }

        boolean negative = false;
        char[] c1 = numStr.toCharArray();
        int i = 0;
        if (radixTable1.indexOf(c1[0]) == -1) {
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
            t = radixTable1.indexOf(c1[i]);
            if (t < 0) {
                throw new NumberFormatException("For input string: \"" + numStr + "\"");
            }
            num = num * radix1 + t;
        }

        do {
            t = num % radix2;
            sb.append(radixTable2.charAt((int) t));
            num /= radix2;
        } while (num != 0);
        if (negative) {
            sb.append("-");
        }

        return sb.reverse().toString();
    }


    public static int max(int... nums) {
        Assert.notEmpty(nums);
        return ArrayUtil.max(nums);
    }


    /**
     * 取最大值，参数不能为空，参数的每个元素都不能为null
     * @param nums
     * @return
     */
    public static BigDecimal max(BigDecimal... nums) {
        return ArrayUtil.max(nums);
    }


    public static BigDecimal min(BigDecimal... nums) {
        return ArrayUtil.min(nums);
    }
}
