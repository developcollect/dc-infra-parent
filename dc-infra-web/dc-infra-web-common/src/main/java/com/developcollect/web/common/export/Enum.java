package com.developcollect.web.common.export;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zhu Kaixiao
 * @date 2020/4/29 9:47
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Enum {

    /**
     * 枚举作用的字段
     */
    String field();

    /**
     * 枚举值
     */
    String[] values();

    /**
     * 枚举值 对应的标签
     */
    String[] labels();

}