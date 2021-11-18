package com.developcollect.spring.validation;



import com.developcollect.core.utils.StrUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/20 17:11
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private Phone anno;

    @Override
    public void initialize(Phone constraintAnnotation) {
        this.anno = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 只做格式校验, 非空校验可以再加@NotBlank
        if (StrUtil.isBlank(value)) {
            return true;
        }

        for (Phone.Type type : anno.types()) {
            if (type.match(value)) {
                return true;
            }
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(getMessage(anno.types(), value)).addConstraintViolation();
        return false;
    }

    private String getMessage(Phone.Type[] types, String value) {
        String msg;
        if (types == null || types.length == 0) {
            msg = "手机号格式错误";
        } else if (anno.types().length > 1) {
            msg = value.charAt(0) != '1' || value.contains("-")
                    ? "座机号格式错误"
                    : "手机号格式错误";
        } else if (anno.types()[0] == Phone.Type.LANDLINE) {
            msg = "座机号格式错误";
        } else {
            msg = "手机号格式错误";
        }
        return msg + ": " + value;
    }
}
