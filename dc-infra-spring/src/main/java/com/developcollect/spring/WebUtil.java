package com.developcollect.spring;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.developcollect.utils.LambdaUtil;
import com.developcollect.utils.ReflectUtil;
import com.developcollect.utils.StrUtil;
import com.developcollect.utils.URLUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Component;
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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * web服务操作工具类
 * 也就是对spring mvc中常用操作的工具
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/10/28 11:42
 */
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class WebUtil {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WebUtil.class);
    private static HttpServletResponse RESPONSE;
    private static HttpServletRequest REQUEST;

    public static final int PORT_DEF = 80;

    private static final String CURR_MAPPING_PATH_ATTRIBUTE_NAME = "currMappingPath";

    @Autowired
    private void init(
            HttpServletRequest request,
            HttpServletResponse response) {
        REQUEST = request;
        RESPONSE = response;
    }


    public static String getMethod(HttpServletRequest request) {
        return request.getMethod().toUpperCase();
    }


    /**
     * 获取当前的request对象
     * 要注意的是在调用该方法的线程如果和controller执行线程不是同一个线程的话
     * 获取的request是无效的
     *
     * @param
     * @return javax.servlet.http.HttpServletRequest
     * @author Zhu Kaixiao
     * @date 2019/10/28 11:37
     */
    public static HttpServletRequest getRequest() {
        return REQUEST;
    }


    /**
     * 获取当前的response对象
     * 要注意的是在调用该方法的线程如果和controller执行线程不是同一个线程的话
     * 获取的response是无效的
     *
     * @param
     * @return javax.servlet.http.HttpServletRequest
     * @author Zhu Kaixiao
     * @date 2019/10/28 11:37
     */
    public static HttpServletResponse getResponse() {
        return RESPONSE;
    }

    /**
     * 获取当前接口的ContextPath
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:21
     */
    public static String currContextPath() {
        return getRequest().getContextPath();
    }


    /**
     * 将一个对象缓存到request中，在后续需要时通过{@link #getAttribute(String)}获取
     * 缓存只在同一个请求中有效，缓存只在会话线程中有效
     *
     * @param name name
     * @param obj  obj
     */
    public static void setAttribute(String name, Object obj) {
        getRequest().setAttribute(name, obj);
    }

    /**
     * 从request中获取一个对象
     *
     * @param name name
     */
    public static <T> T getAttribute(String name) {
        return (T) getRequest().getAttribute(name);
    }

    /**
     * 从request中获取一个对象, 如果获取的对象为null，那么通过defaultValFunc获取一个默认值
     * 并放入request中
     */
    public static <T> T getAttribute(String name, Function<String, T> defaultValFunc) {
        T attribute = getAttribute(name);
        if (attribute == null) {
            attribute = defaultValFunc.apply(name);
            setAttribute(name, attribute);
        }
        return attribute;
    }


    /**
     * 获取当前接口的映射路径
     * 例如：接口是 /admin/users/{id:\\d+}
     * 请求是 /admin/users/1234
     * 那么该方法返回的是/admin/users/{id}
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:22
     */
    public static String currMappingPath() {
        return getMappingPath(getRequest());
    }

    public static String getMappingPath(HttpServletRequest request) {
        String attribute = (String) request.getAttribute(CURR_MAPPING_PATH_ATTRIBUTE_NAME);
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
                mappingPattern = currServletPath();
            }
            // 去除path参数中的正则表达式
            // 例子：/xxx/ddd/{id:\\d+}/{name:\\w+}  ==> /xxx/ddd/{id}/{name}
            mappingPattern = mappingPattern.replaceAll("(?<=\\{)(.*?):.*?(?=})", "$1");
            request.setAttribute(CURR_MAPPING_PATH_ATTRIBUTE_NAME, mappingPattern);
            attribute = mappingPattern;
        }
        return attribute;
    }


    /**
     * 获取接口含模块名的映射路径
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:22
     */
    public static String getModuleMappingPath(HttpServletRequest request) {
        return currModulePrefix() + getMappingPath(request);
    }

    /**
     * 获取当前接口含模块名的映射路径
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:22
     */
    public static String currModuleMappingPath() {
        return currModulePrefix() + currMappingPath();
    }


    /**
     * 获取匹配的路径
     * (模拟dispatcherServlet)
     *
     * @param handlerMappings
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 14:16
     */
    private static String fetchMappingPattern(List<HandlerMapping> handlerMappings, HttpServletRequest request) {
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
                        Iterator<RequestMappingInfo> iterator = requestMappingInfos.iterator();
                        while (iterator.hasNext()) {
                            RequestMappingInfo mappingInfo = iterator.next().getMatchingCondition(request);
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
     * 获取当前访问的接口
     *
     * @param
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/11/26 15:16
     */
    public static String currServletPath() {
        String servletPath = getRequest().getServletPath();
        return servletPath;
    }

    /**
     * 获取当前访问的接口
     * 因为前端调用的接口要经过网关转发, 所以实际访问的接口前面有一个当前服务的名称
     *
     * @param
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:23
     */
    public static String currModulePath() {
        return currModulePrefix() + currServletPath();
    }

    /**
     * 获取当前服务的前缀
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:23
     */
    public static String currModulePrefix() {
        return "/" + SpringUtil.getApplicationName();
    }

    /**
     * 获取当前接口的调用方法
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:23
     */
    public static String currMethod() {
        return getMethod(getRequest());
    }


    /**
     * 获取当前请求的RequestBody
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:24
     */
    public static String currRequestBody() {
        return fetchRequestBody(getRequest());
    }

    /**
     * 提取请求的body
     *
     * @param request
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 10:49
     */
    public static String fetchRequestBody(HttpServletRequest request) {
        String body = null;
        try {
            BufferedReader bufferedReader = request.getReader();
            body = IoUtil.read(bufferedReader);
        } catch (Exception e) {
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
     * @date 2020/10/20 10:43
     */
    public static void print() {
        try {
            HttpServletRequest request = getRequest();

            System.out.println("URL: " + request.getRequestURL());
            System.out.println("QUERY: " + request.getQueryString());
            System.out.println("BODY: " + IoUtil.read(request.getReader()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取当前请求的客户端ip
     *
     * @return 客户端ip
     * @author Zhu Kaixiao
     * @date 2020/10/20 10:42
     */
    public static String getClientIP() {
        return getClientIP(getRequest());
    }

    /**
     * 获取指定请求的客户端ip
     *
     * @return 客户端ip
     * @author Zhu Kaixiao
     * @date 2020/10/20 10:42
     */
    public static String getClientIP(HttpServletRequest request) {
        return ServletUtil.getClientIP(request);
    }

    // region ------------------------------- Header -------------------------------

    /**
     * 获取请求所有的头（header）信息
     *
     * @param request 请求对象{@link HttpServletRequest}
     * @return header值
     */
    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        return ServletUtil.getHeaderMap(request);
    }


    /**
     * 忽略大小写获得请求header中的信息
     *
     * @param request        请求对象{@link HttpServletRequest}
     * @param nameIgnoreCase 忽略大小写头信息的KEY
     * @return header值
     */
    public static String getHeaderIgnoreCase(HttpServletRequest request, String nameIgnoreCase) {
        return ServletUtil.getHeaderIgnoreCase(request, nameIgnoreCase);
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

    public static String getHeaderIgnoreCase(String nameIgnoreCase) {
        return ServletUtil.getHeaderIgnoreCase(getRequest(), nameIgnoreCase);
    }

    /**
     * 获取请求所有的头（header）信息
     *
     * @return header值
     */
    public static Map<String, String> getHeaderMap() {
        return ServletUtil.getHeaderMap(getRequest());
    }

    /**
     * 获得请求header中的信息
     *
     * @param name        头信息的KEY
     * @param charsetName 字符集
     * @return header值
     */
    public static String getHeader(String name, String charsetName) {
        return getHeader(getRequest(), name, CharsetUtil.charset(charsetName));
    }

    /**
     * 获得请求header中的信息
     *
     * @param name    头信息的KEY
     * @param charset 字符集
     * @return header值
     */
    public static String getHeader(String name, Charset charset) {
        return ServletUtil.getHeader(getRequest(), name, charset);
    }

    // endregion ------------------------------- Header -------------------------------

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
     * 客户浏览器是否为IE
     *
     * @return 客户浏览器是否为IE
     */
    public static boolean isIE() {
        return ServletUtil.isIE(getRequest());
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


    public static void addCookie(String key, String value) {
        addCookie(key, value, "/");
    }

    public static void addCookie(String key, String value, String path) {
        addCookie(key, value, path, null);
    }

    public static void addCookie(String key, String value, String path, Integer maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }
        addCookie(cookie);
    }

    public static void addCookie(String key, String value, Integer maxAge) {
        addCookie(key, value, "/", maxAge);
    }

    public static void addCookie(String key, String value, Duration duration) {
        Integer maxAge = duration == null ? null : (int) (duration.toMillis() / 1000);
        addCookie(key, value, "/", maxAge);
    }

    public static void addCookie(Cookie cookie) {
        if (StrUtil.isBlank(cookie.getPath())) {
            cookie.setPath("/");
        }
        getResponse().addCookie(cookie);
    }

    public static String getCookie(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        Cookie[] cookies = getRequest().getCookies();
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

    public static void delCookie(String key) {
        if (StrUtil.isBlank(key)) {
            return;
        }
        Cookie[] cookies = getRequest().getCookies();
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
        return;
    }



    /**
     * 获取请求的接口地址，含参数
     *
     * @param req
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/26 15:19
     */
    public static String getRequestString(HttpServletRequest req) {
        return Stream.of(req.getServletPath(), req.getQueryString())
                .filter(Objects::nonNull)
                .collect(Collectors.joining("?"));
    }

    public static String currRequestString() {
        return getRequestString(getRequest());
    }

    /**
     * 设置让浏览器弹出下载对话框的Header.
     *
     * @param filename 下载后的文件名.
     */
    public static void setDownloadHeader(HttpServletResponse response, String filename) {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + LambdaUtil.raise(() -> encodeFilenameForDownload(getRequest(), filename)));
    }


    /**
     * 下载文件名重新编码
     *
     * @param request  请求对象
     * @param filename 文件名
     * @return 编码后的文件名
     */
    private static String encodeFilenameForDownload(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(filename.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }


    /**
     * 获取请求参数map
     *
     * @param queryString queryString
     * @Title: parseQueryString
     * @return: Map
     */
    public static Map<String, String[]> parseQueryString(String queryString) {
        if (StrUtil.isBlank(queryString)) {
            return Collections.emptyMap();
        }
        Map<String, String[]> queryMap = new TreeMap<String, String[]>();
        String[] params;
        /** &被JsoupUtil转移 */
        if (queryString.indexOf("&amp;") != -1) {
            params = queryString.split("&amp;");
        } else {
            params = queryString.split("&");
        }

        for (String param : params) {
            int index = param.indexOf('=');
            if (index != -1) {
                String name = param.substring(0, index);
                // name为空值不保存
                if (StrUtil.isBlank(name)) {
                    continue;
                }
                String value = param.substring(index + 1);
                try {
                    /**URLDecoder: Incomplete trailing escape (%) pattern*/
                    value = value.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    value = value.replaceAll("\\+", "%2B");
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    log.error("never!", e);
                }
                if (queryMap.containsKey(name)) {
                    String[] values = queryMap.get(name);
                    queryMap.put(name, ArrayUtil.append(values, value));
                } else {
                    queryMap.put(name, new String[]{value});
                }
            }
        }
        return queryMap;
    }

    /**
     * 获取当前访问URL （含协议、域名、端口号[80端口默认忽略]、项目名）
     *
     * @param request HttpServletRequest
     * @Title: getServerUrl
     * @return: String
     */
    public static String getServerUrl(HttpServletRequest request) {
        // 访问协议
        String agreement = request.getScheme();
        // 访问域名
        String serverName = request.getServerName();
        // 访问端口号
        int port = request.getServerPort();
        // 访问项目名
        String contextPath = request.getContextPath();
        String url = "%s://%s%s%s";
        String portStr = "";
        if (port != PORT_DEF) {
            portStr += ":" + port;
        }
        return String.format(url, agreement, serverName, portStr, contextPath);
    }

    public static String getReferer(HttpServletRequest request) {
        return getHeader(request, "Referer");
    }

    public static String currReferer() {
        return getReferer(getRequest());
    }

    /**
     * 获取访问主机， 带协议、域名或ip、端口(协议默认值时没有，如80，443)
     * 注意：这里获取的时优先前端页面的访问主机，在前端页面访问主机无法获取的情况下就获取后端接口的域名
     *
     * @param request request
     * @return 访问域名
     */
    public static String getAccessHost(HttpServletRequest request) {
        // 有origin，直接返回origin, 只在页面跨域或post时才有
        String tmp = getHeader("origin");
        if (StrUtil.isNotBlank(tmp)) {
            return tmp;
        }

        // 有referer，通过referer解析，只在页面调用接口才有
        tmp = getReferer(request);
        if (StrUtil.isNotBlank(tmp)) {
            URL url = URLUtil.url(getReferer(request));
            String accessHost = url.getProtocol() + "://" + url.getHost();
            if (url.getPort() > 0 && url.getPort() != url.getDefaultPort()) {
                accessHost = accessHost + ":" + url.getPort();
            }
            return accessHost;
        }

//        // 有host，通过host解析，http1.1后才有
//        // host是不带协议的
//        tmp = getHeader("host");
//        if (StringUtils.isNotBlank(tmp)) {
//            return request.getScheme() + "://" + tmp;
//        }

        // 都没有，从当前访问路径解析
        int port = request.getServerPort();
        String portStr;
        if ((!request.isSecure() && port == 80) || (request.isSecure() && port == 443)) {
            portStr = "";
        } else {
            portStr = ":" + port;
        }
        return String.format("%s://%s%s", request.getScheme(), request.getServerName(), portStr);
    }


    public static String currAccessHost() {
        return getAccessHost(getRequest());
    }

    public static String getParam(HttpServletRequest request, String name) {
        String[] values = getParamValues(request, name);
        return ArrayUtil.isNotEmpty(values) ? StrUtil.join(',', values) : null;
    }

    /**
     * 获取参数值 数组
     *
     * @param request HttpServletRequest
     * @param name    参数值名称
     * @return String[]
     * @Title getParamValues
     */
    public static String[] getParamValues(HttpServletRequest request, String name) {
        Assert.notNull(request, "Request must not be null");
        String qs = request.getQueryString();
        Map<String, String[]> queryMap = parseQueryString(qs);
        return getParamValues(request, queryMap, name);
    }

    /**
     * 获取参数值 数组
     *
     * @param request  HttpServletRequest
     * @param queryMap Map
     * @param name     参数值名称
     * @return String[]
     * @Title getParamValues
     */
    public static String[] getParamValues(HttpServletRequest request, Map<String, String[]> queryMap, String name) {
        Assert.notNull(request, "Request must not be null");
        String[] values = queryMap.get(name);
        if (values == null) {
            values = request.getParameterValues(name);
        }
        return values;
    }

    /* 设备识别 */

    public static Device currDevice() {
        Device currentDevice = DeviceUtils.getRequiredCurrentDevice(getRequest());
        return currentDevice;
    }
//
//    /**
//     * 判断当前是否访问pcweb端
//     * 注意：
//     * 这里判断是否访问了pc端资源，而不是判断是否pc设备访问
//     *
//     * @return boolean
//     * @author Zhu Kaixiao
//     * @date 2020/11/4 17:25
//     */
//    public static boolean isPcWeb() {
//        return false;
//    }
//
//    public static boolean isH5() {
//        return false;
//    }

    /**
     * 判断当前是否是用pc设备(电脑)访问
     *
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/11/4 17:28
     */
    public static boolean isPc() {
        return currDevice().isNormal();
    }

    /**
     * 判断当前是否是用平板设备(平板电脑)访问
     *
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/11/4 17:28
     */
    public static boolean isTablet() {
        return currDevice().isTablet();
    }

    /**
     * 判断当前是否是用移动设备(手机)访问
     *
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/11/4 17:28
     */
    public static boolean isMobile() {
        return currDevice().isMobile();
    }

//    public static boolean isValidRequestUri(String url) {
//        if (StrUtil.isNotEmpty(url)) {
//            if (hasSpecialChar(url)) {
//                return true;
//            }
//            try {
//                /**尝试decode两次判断是否有特殊字符*/
//                try {
//                    /**URLDecoder: Incomplete trailing escape (%) pattern*/
//                    url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
//                    url = url.replaceAll("\\+", "%2B");
//                    url = URLDecoder.decode(url, "utf-8");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
//                url = url.replaceAll("\\+", "%2B");
//                url = URLDecoder.decode(url, "UTF-8");
//                if (hasSpecialChar(url)) {
//                    return true;
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return false;
//    }
//
//    private static boolean hasSpecialChar(String a) {
//        return a.contains("<") || a.contains(">") || a.contains("\"")
//                || a.contains("'") || a.contains(" and ")
//                || a.contains(" or ") || a.contains("1=1") || a.contains("(") || a.contains(")")
//                || a.contains("{") || a.contains("}") || a.contains("[") || a.contains("]");
//    }


}
