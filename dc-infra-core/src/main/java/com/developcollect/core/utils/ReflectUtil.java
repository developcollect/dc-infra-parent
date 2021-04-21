package com.developcollect.core.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 反射工具类
 *
 * @author zak
 * @since 1.0.0
 */
public class ReflectUtil extends cn.hutool.core.util.ReflectUtil {


    public static Type[] getGenericSuperclass(Class clazz) {
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return actualTypeArguments;
    }

    public static Type getGenericSuperclass(Class clazz, int index) {
        return getGenericSuperclass(clazz)[index];
    }

    public static Class getGenericSuperclassClass(Class clazz, int index) {
        return convertType2Class(getGenericSuperclass(clazz, index));
    }

    public static Class convertType2Class(Type type) {
        Assert.notNull(type, "Type can not be null!");

        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return ((Class) pt.getRawType());
        } else if (type instanceof TypeVariable) {
            TypeVariable tType = (TypeVariable) type;
            try {
                return Class.forName(tType.getGenericDeclaration().toString());
            } catch (ClassNotFoundException e) {
                throw new UtilException(e);
            }
        } else {
            try {
                return Class.forName(type.getTypeName());
            } catch (ClassNotFoundException e) {
                throw new UtilException(e);
            }
        }
    }

    public static <T> T newInstance(Type type, Object... params) {
        final Class<T> aClass = convertType2Class(type);
        return cn.hutool.core.util.ReflectUtil.newInstance(aClass, params);
    }


    /**
     * 设置字段值
     * <p>
     * 原来的方法不支持设置静态字段的值, 所以通过继承覆盖原有方法
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @param value     值，值类型必须与字段类型匹配，不会自动转换对象类型
     * @throws UtilException 包装IllegalAccessException异常
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws UtilException {
        Assert.notNull(obj);
        Assert.notBlank(fieldName);

        boolean ins = obj instanceof Class;

        Field field = getField(ins ? (Class<?>) obj : obj.getClass(), fieldName);
        Assert.notNull(field, "Field [{}] is not exist in [{}]", fieldName,
                ins ? ((Class) obj).getName() : obj.getClass().getName());
        setFieldValue(obj, field, value);
    }

    public static void setFieldValue(Object obj, Field field, Object value) throws UtilException {
        if (field == null) {
            if (obj == null) {
                throw new IllegalArgumentException("Field can not be null !");
            } else {
                final boolean ins = obj instanceof Class;
                final String clazzName = ins ? ((Class) obj).getName() : obj.getClass().getName();
                throw new IllegalArgumentException("Field in [" + clazzName + "] not exist !");
            }
        }

        setAccessible(field);

        Object _obj = obj;
        if (isStatic(field)) {
            _obj = null;
        }

        if (null != value) {
            Class<?> fieldType = field.getType();
            if (false == fieldType.isAssignableFrom(value.getClass())) {
                //对于类型不同的字段，尝试转换，转换失败则使用原对象类型
                final Object targetValue = Convert.convert(fieldType, value);
                if (null != targetValue) {
                    value = targetValue;
                }
            }
        }

        try {
            field.set(_obj, value);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", _obj == null ? obj : obj.getClass(), field.getName());
        }
    }

    /**
     * 判断是否是静态字段
     *
     * @param field 字段对俩
     * @return boolean 如果是静态字段,返回true, 否则返回false
     * @author zak
     * @date 2020/7/6 10:03
     */
    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    /**
     * 判断是否静态方法
     *
     * @param method 方法对象
     * @return boolean 如果是静态方法,返回true, 否则返回false
     * @author zak
     * @date 2020/7/6 10:03
     */
    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * 获取字段值
     *
     * @param obj   对象, 若字段是静态字段, 那么对象可传null
     * @param field 字段
     * @return 字段值
     * @throws UtilException 包装IllegalAccessException异常
     */
    public static Object getFieldValue(Object obj, Field field) throws UtilException {
        Object _obj = obj;
        if (null == field) {
            return null;
        } else if (isStatic(field)) {
            _obj = null;
        } else if (obj == null) {
            return null;
        }


        setAccessible(field);
        Object result;
        try {
            result = field.get(_obj);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", obj.getClass(), field.getName());
        }
        return result;
    }

    public static List<Method> getMethodsByNameIgnoreCase(Class<?> clazz, String methodName) {
        return getMethodsByName(clazz, false, methodName);
    }

    public static List<Method> getMethodsByName(Class<?> clazz, String methodName) {
        return getMethodsByName(clazz, true, methodName);
    }

    public static List<Method> getMethodsByName(Class<?> clazz, boolean ignoreCase, String methodName) {
        if (null == clazz || StrUtil.isBlank(methodName)) {
            return Collections.emptyList();
        }

        List<Method> methodList = Optional
                .ofNullable(getMethods(clazz))
                .map(
                        ms -> Arrays.stream(ms)
                                .filter(m -> StrUtil.equals(methodName, m.getName(), ignoreCase))
                                .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());

        return methodList;
    }

}
