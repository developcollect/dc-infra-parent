package com.developcollect.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 网络相关工具类
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/3/24 13:57
 */
public class NetUtil extends cn.hutool.core.net.NetUtil {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(NetUtil.class);

    /**
     * 判断ip是否内网ip
     *
     * @param ip
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/3/24 13:58
     */
    public static boolean isInnerIP(String ip) {
        if ("localhost".equalsIgnoreCase(ip)) {
            return true;
        }
        return cn.hutool.core.net.NetUtil.isInnerIP(ip);
    }

    /**
     * 判断目标主机是否可以到达
     *
     * @param target  目标  可以是ip, 也可以是域名
     * @param timeout 超时时长  单位: ms
     * @return boolean 可以ping通返回true  否则返回false
     * @author Zhu Kaixiao
     * @date 2020/3/23 17:50
     */
    public static boolean ping(String target, int timeout) {
        boolean status = false;
        if (target != null) {
            try {
                status = InetAddress.getByName(target).isReachable(timeout);
            } catch (Exception e) {
            }
        }
        return status;
    }

    private static final Pattern IP_PATTERN = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");


    /**
     * 获取当前的外网ip地址
     *
     * @return java.lang.String
     * @author zak
     * @date 2020/3/23 14:48
     */
    public static String getInternetIp() {
        String ip;

        ip = getInternetIp1();
        if (ip != null) {
            return ip;
        }

        ip = getInternetIp5();
        if (ip != null) {
            return ip;
        }

        ip = getInternetIp2();
        if (ip != null) {
            return ip;
        }

        ip = getInternetIp3();
        if (ip != null) {
            return ip;
        }

        ip = getInternetIp4();
        if (ip != null) {
            return ip;
        }
        return null;
    }

    private static String getInternetIp1() {
        String ip = null;
        try {
            String post = HttpUtil.post("http://pv.sohu.com/cityjson?ie=utf-8", "");
            JSONObject jo = JSONObject.parseObject(post.substring(19, post.length() - 1));
            ip = (String) jo.get("cip");
        } catch (Exception e) {
        }
        return ip;
    }

    private static String getInternetIp2() {
        String ip = null;
        try {
            ip = HttpUtil.get("http://ip.42.pl/raw");
        } catch (Exception e) {
        }
        return ip;
    }

    private static String getInternetIp3() {
        String ip = null;
        try {
            String ret = HttpUtil.get("http://httpbin.org/ip");
            JSONObject jo = JSONObject.parseObject(ret);
            ip = jo.getString("origin");
        } catch (Exception e) {
        }
        return ip;
    }

    private static String getInternetIp4() {
        String ip = null;
        try {
            String ret = HttpUtil.get("https://api.ipify.org/?format=json");
            JSONObject jo = JSONObject.parseObject(ret);
            ip = jo.getString("ip");
        } catch (Exception e) {
        }
        return ip;
    }

    private static String getInternetIp5() {
        String ip = null;
        try {
            // 这个网站可能每年的域名都会变
            URL url = new URL("http://202020.ip138.com");
            URLConnection urlconn = url.openConnection();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlconn.getInputStream()))) {
                String buf;
                StringBuilder sb = new StringBuilder();
                while ((buf = br.readLine()) != null) {
                    sb.append(buf);
                }
                Matcher matcher = IP_PATTERN.matcher(sb);
                if (matcher.find()) {
                    ip = matcher.group(1);
                }
            }
        } catch (Exception e) {
        }
        return ip;
    }


    /**
     * 根据ip地址获取定位信息
     *
     * @param ip
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/4/30 10:23
     */
    public static String getIpLocation(String ip) {
        try {
            String body = HttpRequest
                    .get(String.format("https://apis.map.qq.com/ws/location/v1/ip?ip=%s&key=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77", ip))
                    .header("Referer", "https://lbs.qq.com/service/webService/webServiceGuide/webServiceIp")
                    .execute()
                    .body();
            final JSONObject jo = JSONObject.parseObject(body);
            if (jo.getIntValue("status") == 0) {
                final JSONObject result = jo.getJSONObject("result");
                final JSONObject adInfo = result.getJSONObject("ad_info");
                final String location = Arrays.asList(
                        adInfo.getString("nation"),
                        adInfo.getString("province"),
                        adInfo.getString("city"),
                        adInfo.getString("district")
                ).stream()
                        .distinct()
                        .filter(StrUtil::isNotBlank)
                        .filter(s -> !"中国".equals(s))
                        .collect(Collectors.joining());
                return location;
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 校验地址是否为ip
     *
     * @param ip
     * @return true 是 false 否
     */
    public static boolean isIp(String ip) {
        return ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
    }

    public static String PATTERN_L2DOMAIN = "\\w*\\.\\w*:";
    public static String PATTERN_IP = "(\\d*\\.){3}\\d*";

    public static String getCookieDomain(String url) {
        /* 以IP形式访问时，返回IP */
        Pattern ipPattern = Pattern.compile(PATTERN_IP);
        Matcher matcher = ipPattern.matcher(url);
        if (matcher.find()) {
            System.out.println("[HttpUtil][getCookieDomain] match ip.");
            return matcher.group();
        }

        /* 以域名访问时，返回二级域名 */
        Pattern pattern = Pattern.compile(PATTERN_L2DOMAIN);
        matcher = pattern.matcher(url);
        if (matcher.find()) {
            System.out.println("[HttpUtil][getCookieDomain] match domain.");
            String domain = matcher.group();
            /* 裁剪一下是因为连着冒号也匹配进去了，唉~ */
            return domain.substring(0, domain.length() - 1);
        }

        return null;
    }
}
