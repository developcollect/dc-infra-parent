package com.developcollect.extra.servlet;


import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;

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

}
