package com.developcollect.spring.condition;


import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnTableCondition.class)
public @interface ConditionalOnTable {

    String value();
}
