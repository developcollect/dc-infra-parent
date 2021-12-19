package com.developcollect.extra.servlet;


import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServletUtil extends cn.hutool.extra.servlet.ServletUtil {


    public static void writeJson(HttpServletResponse response, CharSequence json) {
        write(response, json.toString(), "application/json; charset=utf8");
    }

    public static void writeJson(HttpServletResponse response, Object obj) {
        if (obj instanceof CharSequence) {
            writeJson(response, (CharSequence) obj);
        } else {
            writeJson(response, JSON.toJSONString(obj));
        }
    }


    /**
     * 获取响应所有的头（header）信息
     *
     * @param response 响应对象{@link HttpServletResponse}
     * @return header值
     */
    public static Map<String, String> getHeaderMap(HttpServletResponse response) {
        final Collection<String> names = response.getHeaderNames();
        final Map<String, String> headerMap = new HashMap<>(names.size());

        for (String name : names) {
            headerMap.put(name, response.getHeader(name));
        }

        return headerMap;
    }

}
