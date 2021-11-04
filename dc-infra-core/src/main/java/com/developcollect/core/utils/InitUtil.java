package com.developcollect.core.utils;

import com.developcollect.core.lang.init.Initable;

public class InitUtil {

    /**
     * 创建一个可初始化对象并进行初始化
     * @param initableClass 工具类
     * @param args 参数
     */
    public static <T extends Initable> T init(Class<T> initableClass, Object... args) {
        return init(ReflectUtil.newInstance(initableClass), args);
    }

    /**
     * 初始化一个可初始化对象
     * @param waitInitObj 需要初始化的对象
     * @param args 参数
     */
    public static <T extends Initable> T init(T waitInitObj, Object... args) {
        waitInitObj.init(args);
        return waitInitObj;
    }
}
