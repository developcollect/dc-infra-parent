package com.developcollect.spring.validation;



import com.developcollect.core.utils.StrUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 枚举字段输入验证
 *
 * @author Zhu Kaixiao
 */
public class EnumValidValidator implements ConstraintValidator<EnumValid, Object> {

    private boolean single;
    private boolean repeat;
    private int minSize;
    private int maxSize;
    private List<String> enums;

    @Override
    public void initialize(EnumValid anno) {
        this.single = anno.single();
        this.repeat = anno.repeat();
        this.minSize = anno.minSize();
        this.maxSize = anno.maxSize();
        this.enums = splitStringEnums(anno.enums());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof Integer) {
            return validIntegerValue((Integer) value, context);
        }
        if (value instanceof String) {
            return validStringValue((String) value, context);
        }
        // 如果是其他类型，或者是null，都直接返回true
        // 如果要对null做校验，则再加@NotNull，则这里不控制
        return true;
    }


    private boolean validIntegerValue(Integer value, ConstraintValidatorContext context) {
        // Integer类型的字段，那就只能是单选
        if (enums.contains(String.valueOf(value))) {
            return true;
        }
        replaceMessage(context, "错误的枚举值：" + value);
        return false;
    }

    private boolean validStringValue(String value, ConstraintValidatorContext context) {
        // 空字符串直接返回true，因为空字符串的校验通过@NotBlank注解控制
        if (StrUtil.isBlank(value)) {
            return true;
        }
        List<String> inputEnums = splitStringEnums(value);
        if (inputEnums.isEmpty()) {
            replaceMessage(context, "无有效枚举值：" + value);
            return false;
        }

        // 校验单选设置
        if (this.single) {
            if (inputEnums.size() > 1) {
                replaceMessage(context, "不可输入多个项：" + value);
                return false;
            }
        } else {
            // 校验最小数量设置
            if (this.minSize > 0) {
                if (inputEnums.size() < minSize) {
                    replaceMessage(context, "输入枚举数量不足：" + value + "   minSize:" + minSize);
                    return false;
                }
            }
            // 校验最大数量设置
            if (this.maxSize > 0) {
                if (inputEnums.size() > maxSize) {
                    replaceMessage(context, "输入枚举数量过多：" + value + "   maxSize:" + maxSize);
                    return false;
                }
            }
            // 校验重复设置
            if (!this.repeat) {
                HashSet<String> inputEnumSet = new HashSet<>(inputEnums);
                if (inputEnums.size() != inputEnumSet.size()) {
                    replaceMessage(context, "存在重复输入值：" + value);
                    return false;
                }
            }
        }

        inputEnums.removeAll(this.enums);
        if (!inputEnums.isEmpty()) {
            replaceMessage(context, "错误的枚举值：" + inputEnums);
            return false;
        }
        return true;
    }

    private List<String> splitStringEnums(String enums) {
        return Arrays.stream(enums.split(","))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }

    private void replaceMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
