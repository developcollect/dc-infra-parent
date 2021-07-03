package com.developcollect.web.common.export;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zhu Kaixiao
 * @date 2020/3/14 13:40
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Export {


    Type type() default Type.EXCEL;

    /**
     * 文件名
     * 不用加后缀名, 加了也会自动去掉
     */
    String filename() default "";

    /**
     * excel表中的标题
     */
    String title() default "";

    /**
     * 字段在excel中的表头
     */
    String[] heads();

    /**
     * 需要生成excel的字段
     */
    String[] fields();

    /**
     * 枚举说明，用于在导出时将枚举变量替换为描述
     */
    Enum[] enums() default {};




    enum Type {
        CSV,
        EXCEL,
    }

}
