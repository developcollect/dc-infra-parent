package com.developcollect.utils;


import cn.hutool.core.bean.BeanUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/11/7 15:04
 */
public class URLUtil extends cn.hutool.core.util.URLUtil {


    public static final String PROTOCOL_HTTP = "http://";

    public static final String PROTOCOL_HTTPS = "https://";

    public static final List<String> TOP_DOMAINS = Arrays.asList(
            ".com", ".net", ".cn", ".org", ".info", ".me", ".cc", ".com.cn",
            ".net.cn", ".name", ".mobi", ".pw", ".tv", ".wang", ".club", ".ac.cn",
            ".在线", ".中国", ".中文网", ".xyz", ".vip", ".网址", ".beer", ".work",
            ".fashion", ".luxe", ".yoga", ".top", ".love", ".online", ".购物", ".ltd",
            ".chat", ".group", ".pub", ".run", ".city", ".live", ".pro", ".red",
            ".网店", ".pet", ".kim", ".移动", ".blue", ".ski", ".pink", ".space",
            ".tech", ".host", ".fun", ".site", ".store", ".gold", ".cool", ".email",
            ".life", ".art", ".icu", ".press", ".company", ".ink", ".design", ".wiki",
            ".video", ".plus", ".center", ".fund", ".guru", ".show", ".team", ".today",
            ".world", ".zone", ".social", ".bio", ".black", ".green", ".lotto", ".organic",
            ".poker", ".promo", ".vote", ".archi", ".voto", ".网站", ".商店", ".企业",
            ".娱乐", ".游戏", ".fit", ".website", ".tk", ".asia", ".org.cn", ".space",
            ".tech", ".host", ".fun", ".site", ".store"
    );

    private URLUtil() {

    }


    /**
     * 拼接参数到url上
     * 参数可以是map或bean
     *
     * @param baseUrl
     * @param params
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 10:15
     */
    public static <P> String splice(String baseUrl, P params) {
        if (StrUtil.isBlank(baseUrl)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (!baseUrl.startsWith(PROTOCOL_HTTP) && !baseUrl.startsWith(PROTOCOL_HTTPS)) {
            sb.append(PROTOCOL_HTTP).append(baseUrl);
        } else {
            sb.append(baseUrl);
        }
        if (params == null) {
            return sb.toString();
        }
        Map<? extends Object, ? extends Object> paramMap;
        if (params instanceof Map) {
            paramMap = (Map) params;
        } else {
            paramMap = BeanUtil.beanToMap(params);
        }
        sb.append("?");
        for (Map.Entry<?, ?> entry : paramMap.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || value == null) {
                continue;
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }


    /**
     * 从url中获取文件名，也就是获取url中最后一个/后面的不带参数的值
     *
     * @param uriStr
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/26 10:23
     */
    public static String getFilename(String uriStr) {
        String path = getPath(uriStr);
        return path.substring(path.lastIndexOf("/"));
    }

    /**
     * 处理url url为null返回null，url为空串或以http://或https://开头，则加上http://，其他情况返回原参数。
     *
     * @param url URL
     * @return
     */
    public static String fixUrl(String url) {
        if (url == null) {
            return null;
        }
        url = url.trim();
        if (StrUtil.isNotBlank(url)
                && !StrUtil.startWithIgnoreCase(url, PROTOCOL_HTTP)
                && !StrUtil.startWithIgnoreCase(url, PROTOCOL_HTTPS)) {
            return PROTOCOL_HTTP + url;
        }
        return url;
    }


    /**
     * path部分有空格等特殊字符会报错，要先转义一次
     *
     * @param uriStr
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/11/23 10:39
     */
    public static String getPath(String uriStr) {
        return cn.hutool.core.util.URLUtil.getPath(encode(uriStr));
    }


    public static String getSecondDomain(String domain) {
        String secondDomain = null;
        for (String topDomain : TOP_DOMAINS) {
            if (domain.endsWith(topDomain)) {
                int index = domain.lastIndexOf('.', domain.length() - topDomain.length() - 1);
                secondDomain = domain.substring(index + 1);
                break;
            }
        }
        if (secondDomain == null) {
            int index = domain.lastIndexOf('.', domain.lastIndexOf('.') - 1);
            secondDomain = domain.substring(index + 1);
        }
        return secondDomain;
    }
}
