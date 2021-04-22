package com.developcollect.core.project;

import cn.hutool.core.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 通过读取pom.properties(maven生成的文件)获取当前项目的构建信息（groupId、artifactId、version）
 * 该工具支持源码运行项目和jar包运行项目
 * <p>
 * 注意：
 * 该工具对文件所在目录有严格要求，如果有修改编译或打包配置，影响到pom.properties所在目录，会导致获取失败，将抛出异常
 *
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/1/6 15:51
 */
public class BuildVersion {

    private static volatile Map<String, String> buildInfo;

    public static String getGroupId() {
        return Optional.ofNullable(getBuildInfo()).map(m -> m.get("groupId")).orElse("CantGetGroupId");
    }

    public static String getArtifactId() {
        return Optional.ofNullable(getBuildInfo()).map(m -> m.get("artifactId")).orElse("CantGetArtifactId");
    }

    public static String getVersion() {
        return Optional.ofNullable(getBuildInfo()).map(m -> m.get("version")).orElse("CantGetVersion");
    }

    /**
     * 从maven编译后的target目录下读取构建版本信息
     */
    private static Map<String, String> readPropertiesFromTarget(URL url) {
        String[] split = url.getPath().split("/target/classes/");
        String s = split[0] + "/target/maven-archiver/pom.properties";
        File file = new File(s.substring(1));
        if (!file.exists()) {
            return null;
        }
        HashMap<String, String> map = new HashMap<>(3);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("version=")) {
                    map.put("version", line.substring(8));
                } else if (line.startsWith("groupId=")) {
                    map.put("groupId", line.substring(8));
                } else if (line.startsWith("artifactId=")) {
                    map.put("artifactId", line.substring(11));
                }
            }
        } catch (IOException e) {
        }
        return map.size() == 3 ? map : null;
    }

    private static Map<String, String> readPropertiesFromPom(URL url) {
        String[] split = url.getPath().split("/target/classes/");
        String s = split[0] + "/pom.xml";
        File pomFile = new File(s.substring(1));
        if (!pomFile.exists()) {
            return null;
        }
        Document document = XmlUtil.readXML(pomFile);
        Element documentElement = document.getDocumentElement();
        NodeList childNodes = documentElement.getChildNodes();
        String artifactId = null;
        String groupId = null;
        String version = null;
        Node parentTagNode = null;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if ("artifactId".equals(item.getNodeName())) {
                artifactId = item.getTextContent();
            }
            if ("groupId".equals(item.getNodeName())) {
                groupId = item.getTextContent();
            }
            if ("version".equals(item.getNodeName())) {
                version = item.getTextContent();
            }
            if ("parent".equals(item.getNodeName())) {
                parentTagNode = item;
            }
            if (artifactId != null && groupId != null && version != null) {
                break;
            }
        }
        if (parentTagNode != null) {
            NodeList nodes = parentTagNode.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node item = nodes.item(i);
                if (groupId == null && "groupId".equals(item.getNodeName())) {
                    groupId = item.getTextContent();
                }
                if (version == null && "version".equals(item.getNodeName())) {
                    version = item.getTextContent();
                }
            }
        }


        if (groupId == null || artifactId == null || version == null) {
            return null;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("artifactId", artifactId);
        map.put("version", version);
        return map;
    }

    /**
     * 从maven编译的jar包中读取构建信息
     * spring boot构建的jar包在Manifest中有版本信息
     */
    private static Map<String, String> readPropertiesFromJar(URL url) {
        JarFile jarFile = null;
        try {
            URLConnection urlConnection = url.openConnection();
            JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
            jarFile = jarURLConnection.getJarFile();
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();

            String version = mainAttributes.getValue("Implementation-Version");
            String groupId = mainAttributes.getValue("Implementation-Vendor-Id");
            String artifactId = mainAttributes.getValue("Implementation-Title");

            if (version != null && groupId != null && artifactId != null) {
                HashMap<String, String> map = new HashMap<>(3);
                map.put("version", version);
                map.put("groupId", groupId);
                map.put("artifactId", artifactId);
                return map;
            }
        } catch (Exception e) {
        } finally {
            try {
                if (jarFile != null) {
                    jarFile.close();
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }


    //private static Map<String, String> readPropertiesFromJar(URL url) {
    //        JarFile jarFile = null;
    //        try {
    //            String s = url.toString();
    //
    //            if (!s.endsWith(".jar!/")) {
    //                s = s.substring(0, s.lastIndexOf(".jar!/") + 6);
    //            }
    //            s += "META-INF/maven!/";
    //            url = new URL(s);
    //
    //            URLConnection urlConnection = url.openConnection();
    //            JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
    //            jarFile = jarURLConnection.getJarFile();
    //            Manifest manifest = jarFile.getManifest();
    //            Attributes mainAttributes = manifest.getMainAttributes();
    //            for (Map.Entry<Object, Object> entry : mainAttributes.entrySet()) {
    //                System.out.println(entry.getKey() + " ===> " + entry.getValue());
    //            }
    //
    //            Enumeration<JarEntry> entries = jarFile.entries();
    //            String pomProp = null;
    //            while (entries.hasMoreElements()) {
    //                JarEntry entry = entries.nextElement();
    //                if (entry.isDirectory()) {
    //                    continue;
    //                }
    //                if (entry.getName().endsWith("pom.properties")) {
    //                    if (pomProp != null) {
    //                        throw new IllegalStateException("有多个pom.properties, 无法确定！");
    //                    }
    //                    pomProp = entry.getName();
    //                }
    //            }
    //            return new Setting(new URL(s + pomProp), StandardCharsets.UTF_8, false);
    //        } catch (IOException e) {
    //            throw new IllegalStateException("无法读取构建版本信息", e);
    //        } finally {
    //            try {
    //                if (jarFile != null) {
    //                    jarFile.close();
    //                }
    //            } catch (IOException ignored) {
    //            }
    //        }
    //    }


    public static Map<String, String> getBuildInfo() {
        if (buildInfo == null) {
            synchronized (BuildVersion.class) {
                if (buildInfo == null) {
                    // 1. 源码运行
                    // 2. jar运行

                    try {
                        Class cls = BuildVersion.class;
                        URL url = cls.getResource("");
                        Map<String, String> map2 = null;
                        if ("jar".equals(url.getProtocol())) {
                            map2 = readPropertiesFromJar(url);
                        } else {
                            try {
                                map2 = readPropertiesFromTarget(url);
                            } catch (Exception ignore) {
                            }
                            if (map2 == null) {
                                map2 = readPropertiesFromPom(url);
                            }
                        }
                        if (map2 != null) {
                            buildInfo = Collections.unmodifiableMap(map2);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
        return buildInfo;
    }
}