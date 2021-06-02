package com.developcollect.spring;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.developcollect.core.utils.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;


@Slf4j
public class WebUtil {

    private static HttpServletRequest request;
    private static HttpServletResponse response;

    private static final String CURR_MAPPING_PATH_ATTRIBUTE_NAME = "currMappingPath";

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

    public static String getMethod(HttpServletRequest request) {
        return request.getMethod().toUpperCase();
    }

    public static String getMethod() {
        return getMethod(getRequest());
    }

    /**
     * 是否为GET请求
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为GET请求
     */
    public static boolean isGetMethod(HttpServletRequest request) {
        return ServletUtil.isGetMethod(request);
    }

    /**
     * 是否为POST请求
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为POST请求
     */
    public static boolean isPostMethod(HttpServletRequest request) {
        return ServletUtil.isPostMethod(request);
    }

    /**
     * 是否为Multipart类型表单，此类型表单用于文件上传
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 是否为Multipart类型表单，此类型表单用于文件上传
     */
    public static boolean isMultipart(HttpServletRequest request) {
        return ServletUtil.isMultipart(request);
    }

    /**
     * 是否为GET请求
     *
     * @return 是否为GET请求
     */
    public static boolean isGetMethod() {
        return ServletUtil.isGetMethod(getRequest());
    }

    /**
     * 是否为POST请求
     *
     * @return 是否为POST请求
     */
    public static boolean isPostMethod() {
        return ServletUtil.isPostMethod(getRequest());
    }

    /**
     * 是否为Multipart类型表单，此类型表单用于文件上传
     *
     * @return 是否为Multipart类型表单，此类型表单用于文件上传
     */
    public static boolean isMultipart() {
        return ServletUtil.isMultipart(getRequest());
    }


    public static String getContextPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    /**
     * 获取当前接口的ContextPath
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     */
    public static String getContextPath() {
        return getContextPath(getRequest());
    }

    public static String getServletPath(HttpServletRequest request) {
        return request.getServletPath();
    }

    /**
     * 获取当前访问的接口
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     */
    public static String getServletPath() {
        return getServletPath(getRequest());
    }

    public static String getClientIp() {
        return getClientIp(getRequest());
    }

    public static String getClientIp(HttpServletRequest request) {
        return ServletUtil.getClientIP(request);
    }

    /**
     * 将一个对象缓存到request中，在后续需要时通过{@link #getAttribute(String)}获取
     * 缓存只在同一个请求中有效，缓存只在会话线程中有效
     *
     * @param name name
     * @param obj  obj
     */
    public static void setAttribute(String name, Object obj) {
        setAttribute(getRequest(), name, obj);
    }

    public static void setAttribute(HttpServletRequest request, String name, Object obj) {
        request.setAttribute(name, obj);
    }


    /**
     * 从request中获取一个对象
     *
     * @param name name
     */
    public static <T> T getAttribute(HttpServletRequest request, String name) {
        return (T) request.getAttribute(name);
    }

    /**
     * 从request中获取一个对象, 如果获取的对象为null，那么通过defaultValFunc获取一个默认值
     * 并放入request中
     */
    public static <T> T getAttribute(HttpServletRequest request, String name, Function<String, T> defaultValFunc) {
        T attribute = getAttribute(request, name);
        if (attribute == null) {
            attribute = defaultValFunc.apply(name);
            setAttribute(name, attribute);
        }
        return attribute;
    }

    /**
     * 从request中获取一个对象
     *
     * @param name name
     */
    public static <T> T getAttribute(String name) {
        return getAttribute(getRequest(), name);
    }

    /**
     * 从request中获取一个对象, 如果获取的对象为null，那么通过defaultValFunc获取一个默认值
     * 并放入request中
     */
    public static <T> T getAttribute(String name, Function<String, T> defaultValFunc) {
        return getAttribute(getRequest(), name, defaultValFunc);
    }


    /**
     * 获取当前接口的映射路径
     * 例如：接口是 /admin/users/{id:\\d+}
     * 请求是 /admin/users/1234
     * 那么该方法返回的是/admin/users/{id}
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     */
    public static String getMappingPath(HttpServletRequest request) {
        String attribute = getAttribute(request, CURR_MAPPING_PATH_ATTRIBUTE_NAME);
        if (StrUtil.isBlank(attribute)) {
            String mappingPattern = null;
            Map<String, DispatcherServlet> dispatcherServletMap = SpringUtil.getBeansOfType(DispatcherServlet.class);
            for (DispatcherServlet dispatcherServlet : dispatcherServletMap.values()) {
                List<HandlerMapping> handlerMappings = dispatcherServlet.getHandlerMappings();
                mappingPattern = fetchMappingPattern(handlerMappings, request);
                if (mappingPattern != null) {
                    break;
                }
            }
            if (mappingPattern == null) {
                mappingPattern = getServletPath();
            }
            // 去除path参数中的正则表达式
            // 例子：/xxx/ddd/{id:\\d+}/{name:\\w+}  ==> /xxx/ddd/{id}/{name}
            mappingPattern = mappingPattern.replaceAll("(?<=\\{)(.*?):.*?(?=})", "$1");
            setAttribute(request, CURR_MAPPING_PATH_ATTRIBUTE_NAME, mappingPattern);
            attribute = mappingPattern;
        }
        return attribute;
    }

    public static String getMappingPath() {
        return getMappingPath(getRequest());
    }

    /**
     * 获取匹配的路径
     * (模拟dispatcherServlet)
     *
     * @param handlerMappings
     * @return java.lang.String
     * @author Zhu Kaixiao
     */
    private static String fetchMappingPattern(List<HandlerMapping> handlerMappings, HttpServletRequest request) {
        if (handlerMappings == null) {
            return null;
        }
        for (HandlerMapping handlerMapping : handlerMappings) {
            try {
                if (handlerMapping instanceof AbstractHandlerMethodMapping) {
                    AbstractHandlerMethodMapping ahm = (AbstractHandlerMethodMapping) handlerMapping;
                    String lookupPath = ahm.getUrlPathHelper().getLookupPathForRequest(request);
                    // AbstractHandlerMethodMapping.MappingRegistry
                    Object mappingRegistry = ReflectUtil.invoke(ahm, "getMappingRegistry");
                    List<RequestMappingInfo> mappingInfos = ReflectUtil.invoke(mappingRegistry, "getMappingsByUrl", lookupPath);
                    if (mappingInfos == null) {
                        Map<RequestMappingInfo, HandlerMethod> mappings = ReflectUtil.invoke(mappingRegistry, "getMappings");
                        Set<RequestMappingInfo> requestMappingInfos = mappings.keySet();
                        for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                            RequestMappingInfo mappingInfo = requestMappingInfo.getMatchingCondition(request);
                            String pattern = fetchPatternFromRequestMappingInfo(mappingInfo);
                            if (pattern != null) {
                                return pattern;
                            }
                        }
                    }
                    if (mappingInfos != null) {
                        for (RequestMappingInfo mappingInfo : mappingInfos) {
                            String pattern = fetchPatternFromRequestMappingInfo(mappingInfo);
                            if (pattern != null) {
                                return pattern;
                            }
                        }
                    }

                } else if (handlerMapping instanceof AbstractUrlHandlerMapping) {
                    AbstractUrlHandlerMapping urlHandlerMapping = (AbstractUrlHandlerMapping) handlerMapping;
                    String lookupPath = urlHandlerMapping.getUrlPathHelper().getLookupPathForRequest(request);
                    if (urlHandlerMapping.getHandlerMap().containsKey(lookupPath)) {
                        return lookupPath;
                    }
                    for (String registeredPattern : urlHandlerMapping.getHandlerMap().keySet()) {
                        if (urlHandlerMapping.getPathMatcher().match(registeredPattern, lookupPath)) {
                            return registeredPattern;
                        } else if (urlHandlerMapping.useTrailingSlashMatch()) {
                            if (!registeredPattern.endsWith("/") && urlHandlerMapping.getPathMatcher().match(registeredPattern + "/", lookupPath)) {
                                return registeredPattern;
                            }
                        }
                    }
                }
            } catch (Throwable ignore) {
            }
        }

        return null;
    }


    private static String fetchPatternFromRequestMappingInfo(RequestMappingInfo requestMappingInfo) {
        Set<String> patterns = Optional
                .ofNullable(requestMappingInfo)
                .map(RequestMappingInfo::getPatternsCondition)
                .map(PatternsRequestCondition::getPatterns)
                .orElse(Collections.emptySet());
        Iterator<String> iterator = patterns.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }



    /**
     * 获取当前请求的RequestBody
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     */
    public static String getRequestBody() {
        return getRequestBody(getRequest());
    }

    /**
     * 提取请求的body
     *
     * @param request
     * @return java.lang.String
     * @author Zhu Kaixiao
     */
    public static String getRequestBody(HttpServletRequest request) {
        String body = null;
        try {
            BufferedReader bufferedReader = request.getReader();
            body = IoUtil.read(bufferedReader);
        } catch (Exception ignore) {
        }
        return body;
    }

    /**
     * 在控制台打印出请求的信息
     * 包括请求的地址，参数，body
     * <p>
     * 仅用于开发时测试
     *
     * @author Zhu Kaixiao
     */
    public static void print(HttpServletRequest request) {
        try {
            System.out.println("URL: " + request.getRequestURL());
            System.out.println("QUERY: " + request.getQueryString());
            System.out.println("BODY: " + IoUtil.read(request.getReader()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取请求所有的头（header）信息
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return header值
     */
    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        return ServletUtil.getHeaderMap(request);
    }

    public static Map<String, String> getHeaderMap() {
        return ServletUtil.getHeaderMap(getRequest());
    }

    /**
     * 忽略大小写获得请求header中的信息
     *
     * @param request        请求对象{@link HttpServletRequest}
     * @param name 忽略大小写头信息的KEY
     * @return header值
     */
    public static String getHeaderIgnoreCase(HttpServletRequest request, String name) {
        return ServletUtil.getHeaderIgnoreCase(request, name);
    }

    public static String getHeaderIgnoreCase(String name) {
        return ServletUtil.getHeaderIgnoreCase(getRequest(), name);
    }


    /**
     * 获得请求header中的信息
     *
     * @param request     请求对象{@link HttpServletRequest}
     * @param name        头信息的KEY
     * @param charsetName 字符集
     * @return header值
     */
    public static String getHeader(HttpServletRequest request, String name, String charsetName) {
        return getHeader(request, name, CharsetUtil.charset(charsetName));
    }

    /**
     * 获得请求header中的信息
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @param name    头信息的KEY
     * @param charset 字符集
     * @return header值
     */
    public static String getHeader(HttpServletRequest request, String name, Charset charset) {
        return ServletUtil.getHeader(request, name, charset);
    }

    public static String getHeader(HttpServletRequest request, String name) {
        return ServletUtil.getHeader(request, name, StandardCharsets.UTF_8);
    }

    public static String getHeader(String name) {
        return ServletUtil.getHeader(getRequest(), name, StandardCharsets.UTF_8);
    }

    public static String getHeader(String name, Charset charset) {
        return ServletUtil.getHeader(getRequest(), name, charset);
    }

    public static String getHeader(String name, String charsetName) {
        return ServletUtil.getHeader(getRequest(), name, charsetName);
    }

    public static String getReferer() {
        return getReferer(getRequest());
    }

    public static String getReferer(HttpServletRequest request) {
        return getHeader(request, "referer");
    }


    /**
     * 客户浏览器是否为IE
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return 客户浏览器是否为IE
     */
    public static boolean isIE(HttpServletRequest request) {
        return ServletUtil.isIE(request);
    }

    /**
     * 客户浏览器是否为IE
     *
     * @return 客户浏览器是否为IE
     */
    public static boolean isIE() {
        return ServletUtil.isIE(getRequest());
    }


    public static void addCookie(String key, String value) {
        addCookie(key, value, "/");
    }

    public static void addCookie(HttpServletResponse response, String key, String value) {
        addCookie(response, key, value, "/");
    }

    public static void addCookie(HttpServletResponse response, String key, String value, String path) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        addCookie(response, cookie);
    }


    public static void addCookie(String key, String value, String path) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, String key, String value, String path, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        addCookie(response, cookie);
    }

    public static void addCookie(String key, String value, String path, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        addCookie(cookie);
    }

    public static void addCookie(String key, String value, int maxAge) {
        addCookie(key, value, "/", maxAge);
    }

    public static void addCookie(HttpServletResponse response, String key, String value, int maxAge) {
        addCookie(response, key, value, "/", maxAge);
    }

    public static void addCookie(HttpServletResponse response, String key, String value, Duration duration) {
        if (duration == null) {
            addCookie(response, key, value, "/");
        } else {
            int maxAge = (int) (duration.toMillis() / 1000);
            addCookie(response, key, value, "/", maxAge);
        }
    }

    public static void addCookie(String key, String value, Duration duration) {
        addCookie(getResponse(), key, value, duration);
    }

    public static void addCookie(HttpServletResponse response, Cookie cookie) {
        if (StrUtil.isBlank(cookie.getPath())) {
            cookie.setPath("/");
        }
        response.addCookie(cookie);
    }

    public static void addCookie(Cookie cookie) {
        addCookie(getResponse(), cookie);
    }

    public static String getCookie(HttpServletRequest request, String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String getCookie(String key) {
        return getCookie(getRequest(), key);
    }

    public static void delCookie(HttpServletRequest request, String key) {
        if (StrUtil.isBlank(key)) {
            return;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                cookie.setMaxAge(0);
                cookie.setValue(null);
                addCookie(cookie);
                return;
            }
        }
    }

    public static void delCookie(String key) {
        delCookie(getRequest(), key);
    }

}
