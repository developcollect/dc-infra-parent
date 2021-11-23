package com.developcollect.core.utils;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Filter;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.developcollect.core.geo.GeoUtil;
import com.developcollect.core.geo.Location;
import com.developcollect.core.geo.tencent.TencentLocationUtil;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static Location getIpLocation(String ip) {
        return GeoUtil.getIpLocation(ip);
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


    public static String localIpv4() {
        try {
            LinkedHashSet<InetAddress> inetAddresses = localIpv4AddressList();
            InetAddress next = inetAddresses.iterator().next();
            return next.getHostAddress();
        } catch (Exception e) {
        }
        return "";
    }


    public static LinkedHashSet<InetAddress> localAddressList(Filter<InetAddress> addressFilter) {

        LinkedHashSet<NetworkInterface> interfaces = null;
        try {
            interfaces = localNetworkInterfaceList(networkInterface ->
                    !networkInterface.getDisplayName().contains("Hyper-V") && !networkInterface.getDisplayName().contains("VMware"));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        final LinkedHashSet<InetAddress> ipSet = new LinkedHashSet<>();

        for (NetworkInterface networkInterface : interfaces) {
            final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                final InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress != null && (null == addressFilter || addressFilter.accept(inetAddress))) {
                    ipSet.add(inetAddress);
                }
            }
        }

        return ipSet;
    }


    public static LinkedHashSet<InetAddress> localAddressList() throws SocketException {
        return localAddressList(null);
    }

    public static LinkedHashSet<InetAddress> localIpv4AddressList() throws SocketException {
        return localAddressList(t -> t instanceof Inet4Address);
    }


    public static LinkedHashSet<String> localMacs() throws SocketException {
        LinkedHashSet<NetworkInterface> interfaces = localNetworkInterfaceList(networkInterface ->
                !networkInterface.getDisplayName().contains("Hyper-V") && !networkInterface.getDisplayName().contains("VMware"));

        final LinkedHashSet<String> macSet = new LinkedHashSet<>();

        for (NetworkInterface networkInterface : interfaces) {
            byte[] hardwareAddress = networkInterface.getHardwareAddress();
            macSet.add(bytesToMac(hardwareAddress));
        }

        return macSet;
    }

    public static LinkedHashSet<NetworkInterface> localNetworkInterfaceList(Filter<NetworkInterface> interfaceFilter) throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        if (networkInterfaces == null) {
            throw new UtilException("Get network interface error!");
        }

        final LinkedHashSet<NetworkInterface> interfaceSet = new LinkedHashSet<>();

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();

            byte[] hardwareAddress = networkInterface.getHardwareAddress();
            if (hardwareAddress != null && (interfaceFilter == null || interfaceFilter.accept(networkInterface))) {
                interfaceSet.add(networkInterface);
            }
        }

        return interfaceSet;
    }


    public static String bytesToMac(byte[] mac) {

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }

            //mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            sb.append(s.length() == 1 ? 0 + s : s);
        }

        return sb.toString().toUpperCase();
    }

    public static String bytesToIpv4(byte[] bytes) {
        return (bytes[0] & 0xff) + "." + (bytes[1] & 0xff) + "." + (bytes[2] & 0xff) + "." + (bytes[3] & 0xff);
    }

}
