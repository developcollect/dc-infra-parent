package com.developcollect.web.ssm.utils;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.developcollect.core.utils.LambdaUtil;
import com.developcollect.core.utils.ReflectUtil;
import com.developcollect.core.utils.StrUtil;

import java.lang.reflect.Field;

public class EntityUtil {

    public static String getColumnName(Class entityClass, SFunction sf) {
        return getColumnName(entityClass, LambdaUtil.getFieldName(sf));
    }

    public static String getColumnName(Class entityClass, String fieldName) {
        // 先尝试通过注解获取
        Field field = ReflectUtil.getField(entityClass, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("field not exist: fieldName:" + fieldName + " class:" + entityClass);
        }

        String columnName = null;
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null) {
            columnName = tableField.value();
        }
        // 如果注解中没有指定列名，则直接转换成下划线格式
        if (StrUtil.isBlank(columnName)) {
            columnName = StrUtil.toUnderlineCase(fieldName);
        }
        return columnName;
    }
}
