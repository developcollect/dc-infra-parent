package com.developcollect.core.utils;


import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;

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
}
