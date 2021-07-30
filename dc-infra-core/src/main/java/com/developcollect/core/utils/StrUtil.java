package com.developcollect.core.utils;


import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author zak
 * @version 1.0
 */
public class StrUtil extends cn.hutool.core.util.StrUtil {


    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @return 连接后的字符串
     * @see IterUtil#join(Iterator, CharSequence)
     */
    public static <T> String join(CharSequence conjunction, Iterable<T> iterable) {
        return CollectionUtil.join(iterable, conjunction);
    }


    public static <T> String join(char conjunction, Object[] iterable) {
        return ArrayUtil.join(iterable, String.valueOf(conjunction));
    }


    /**
     * 返回指定字符串的左边的指定长度的子串，如果长度超过原字符串长度，则返回原字符串
     * @param cs 字符串
     * @param len 长度
     */
    public static String left(CharSequence cs, int len) {
        return cs.length() > len ? cs.subSequence(0, len).toString() : cs.toString();
    }

    /**
     * 返回指定字符串的右边的指定长度的子串，如果长度超过原字符串长度，则返回原字符串
     * @param cs 字符串
     * @param len 长度
     */
    public static String right(CharSequence cs, int len) {
        int length = cs.length();
        return length > len ? cs.subSequence(length - len, length).toString() : cs.toString();
    }

    /**
     * 打乱一个字符串
     */
    public static String shuffle(CharSequence cs) {
        char[] chars = cs.toString().toCharArray();
        ArrayUtil.shuffle(chars);
        return String.valueOf(chars);
    }

    /**
     * 判断字符串是否全是数字
     * 不含小数、正负号，只判断数字
     */
    public static boolean isDigits(CharSequence cs) {
        for (int i = 0; i < cs.length(); i++) {
            if (Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * 替换指定字符串的指定区间内字符为"*"
     * 俗称：脱敏功能，后面其他功能，可以见：DesensitizedUtils(脱敏工具类)
     *
     * <pre>
     * StrUtil.hide(null,*,*)=null
     * StrUtil.hide("",0,*)=""
     * StrUtil.hide("jackduan@163.com",-1,4)   ****duan@163.com
     * StrUtil.hide("jackduan@163.com",2,3)    ja*kduan@163.com
     * StrUtil.hide("jackduan@163.com",3,2)    jackduan@163.com
     * StrUtil.hide("jackduan@163.com",16,16)  jackduan@163.com
     * StrUtil.hide("jackduan@163.com",16,17)  jackduan@163.com
     * </pre>
     *
     * @param str          字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @param startSize    星号的数量
     * @return 替换后的字符串
     */
    public static String hide(CharSequence str, int startInclude, int endExclude, int startSize) {
        return replace(str, startInclude, endExclude, '*', startSize);
    }

    /**
     * 替换指定字符串的指定区间内字符为固定字符
     *
     * @param str          字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @param replacedChar 被替换的字符
     * @param replacedSize 被替换的字符的数量
     * @return 替换后的字符串
     */
    public static String replace(CharSequence str, int startInclude, int endExclude, char replacedChar, int replacedSize) {
        // 被替换的字符的数量小于0, 则替换了多少个就填多少个
        if (replacedSize < 0) {
            return replace(str, startInclude, endExclude, replacedChar);
        }
        if (startInclude < 0) {
            startInclude = 0;
        }
        if (isEmpty(str)) {
            return str(str);
        }
        final int strLength = str.length();
        if (startInclude > strLength) {
            return str(str);
        }
        if (endExclude > strLength || endExclude < 0) {
            endExclude = strLength;
        }
        if (startInclude > endExclude) {
            // 如果起始位置大于结束位置，不替换
            return str(str);
        }



        final char[] chars = new char[startInclude + replacedSize + (strLength - endExclude)];
        for (int i = 0; i < chars.length; i++) {
            if (i < startInclude) {
                chars[i] = str.charAt(i);
            } else if (i < (startInclude + replacedSize)) {
                chars[i] = replacedChar;
            } else {
                chars[i] = str.charAt(i + (endExclude - startInclude - replacedSize));
            }
        }
        return new String(chars);
    }

    /**
     * 返回由指定个数的相同的字符组成的字符串
     * @param c 字符
     * @param size 数量
     * @return 创建的字符串
     */
    public static String str(char c, int size) {
        char[] chars = new char[size];
        Arrays.fill(chars, c);
        return String.valueOf(chars);
    }
}
