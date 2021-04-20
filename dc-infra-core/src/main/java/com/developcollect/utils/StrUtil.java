package com.developcollect.utils;


import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.ArrayUtil;

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


}
