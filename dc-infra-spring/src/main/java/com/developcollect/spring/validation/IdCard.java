package com.developcollect.spring.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 身份证号验证
 * 支持：
 * 18位二代大陆身份证号
 * 15位一代大陆身份证号
 * 10位港澳台身份证号
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/10/21 10:25
 */
@Documented
@Constraint(validatedBy = {IdCardValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface IdCard {

    String message() default "身份证号格式错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
