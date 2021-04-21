package com.developcollect.core.utils;

import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/10/22 15:49
 */
public class BeanUtil extends cn.hutool.core.bean.BeanUtil {


    public static Map<String, String> beanToStrMap(Object obj) {
        Map<String, Object> map = beanToMap(obj);
        if (map == null) {
            return null;
        }
        Map<String, String> strMap = new HashMap<>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            strMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return strMap;
    }

    public static void copyPropertiesIgnoreNullValue(Object source, Object target) {
        BeanCopier.create(source, target, CopyOptions.create().ignoreNullValue()).copy();
    }

}
