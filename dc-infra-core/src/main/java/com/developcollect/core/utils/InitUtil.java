package com.developcollect.core.utils;

import com.developcollect.core.lang.init.Initable;

public class InitUtil {

    /**
     * 初始化一个工具类
     * @param utilClass 工具类
     * @param args 参数
     */
    public static <T extends Initable> void init(Class<T> utilClass, Object... args) {
        init(ReflectUtil.newInstance(utilClass), args);
    }

    /**
     * 初始化一个可初始化对象
     * @param waitInitObj 需要初始化的对象
     * @param args 参数
     */
    public static void init(Initable waitInitObj, Object... args) {
        waitInitObj.init(args);
    }
}
