package com.developcollect.spring.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 手机号或座机号
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/20 17:04
 */
@Documented
@Constraint(validatedBy = {PhoneValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface Phone {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 号码类型，支持手机号、座机号
     */
    Type[] types() default Type.MOBILE;

    enum Type{
        /**
         * 座机
         */
        LANDLINE {
            @Override
            boolean match(String val) {
                // 区号3-4位-电话号7-8位-分机号1-6位，分机号可选
                return val.matches("^\\d{3,4}-\\d{7,8}(-\\d{1,6})?$");
            }
        },

        /**
         * 手机
         */
        MOBILE {
            @Override
            boolean match(String val) {
                return val.matches("^1\\d{10}$");
            }
        };


        boolean match(String val) {
            throw new IllegalStateException();
        }


    }

}
