package com.developcollect.spring.validation;

import cn.hutool.core.util.IdcardUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/10/21 10:25
 */
public class IdCard18Validator implements ConstraintValidator<IdCard18, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return IdcardUtil.isValidCard18(s);
    }
}
