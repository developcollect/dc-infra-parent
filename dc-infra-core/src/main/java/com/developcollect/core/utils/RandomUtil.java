package com.developcollect.core.utils;

import cn.hutool.core.lang.WeightRandom;

import java.util.function.ToDoubleFunction;

public class RandomUtil extends cn.hutool.core.util.RandomUtil {

    /**
     * 用于随机选的符号
     */
    public static final String BASE_SYMBOL = "\\!@#$%^&*()_+=-}{[] ';\":<>?/|.,?`~";

    /**
     * 用于随机选的大写字母
     */
    public static final String BASE_UPPER_CHAR = BASE_CHAR.toUpperCase();

    /**
     * 用于随机选的字符，包含数字、大小写字母、符号
     */
    public static final String BASE_ALL = BASE_NUMBER + BASE_CHAR + BASE_UPPER_CHAR + BASE_SYMBOL;

    /**
     * 带有权重的随机生成器
     *
     * @param <T>          随机对象类型
     * @param iter         原始元素集合
     * @param weightGetter 权重的获取方法
     * @return {@link WeightRandom}
     * @since 4.0.3
     */
    public static <T> WeightRandom<T> weightRandom(Iterable<T> iter, ToDoubleFunction<T> weightGetter) {
        WeightRandom<T> weightRandom = new WeightRandom<>();
        for (T obj : iter) {
            weightRandom.add(obj, weightGetter.applyAsDouble(obj));
        }
        return weightRandom;
    }

}
