package com.developcollect.web.utils;

import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebUtil {

    private static HttpServletRequest request;
    private static HttpServletResponse response;

    @Autowired
    private void init(HttpServletRequest request, HttpServletResponse response) {
        WebUtil.request = request;
        WebUtil.response = response;
    }

    public static HttpServletRequest getRequest() {
        return request;
    }

    public static HttpServletResponse getResponse() {
        return response;
    }

    public static String getClientIp() {
        return getClientIp(getRequest());
    }

    public static String getClientIp(HttpServletRequest request) {
        return ServletUtil.getClientIP(request);
    }
}
