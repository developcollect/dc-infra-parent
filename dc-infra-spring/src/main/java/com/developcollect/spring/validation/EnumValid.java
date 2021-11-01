package com.developcollect.spring.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * 枚举字段输入验证
 *
 * @author Zhu Kaixiao
 */
@Documented
@Constraint(validatedBy = {EnumValidValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface EnumValid {

    String message() default "枚举输入错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 可用枚举值
     */
    String enums();

    /**
     * 是否单选
     */
    boolean single() default true;

    /**
     * 是否可输入重复项
     */
    boolean repeat() default false;

    /**
     * 最小输入枚举数量，0表示不限制
     */
    int minSize() default 0;

    /**
     * 最大输入枚举数量，0表示不限制
     */
    int maxSize() default 0;
}
