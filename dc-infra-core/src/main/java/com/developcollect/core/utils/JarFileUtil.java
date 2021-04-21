package com.developcollect.core.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/10/22 17:30
 */
@SuppressWarnings("rawtypes")
public class JarFileUtil {

    private static final Logger log = LoggerFactory.getLogger(JarFileUtil.class);

    /**
     * 复制jar包中的资源到指定位置
     */
    public static void copy(String src, String dest, Class clazz, boolean descIsFilename) {
        try {
            // 通过class定位jar包
            URL url = clazz.getResource(src);
            if (url == null) {
                throw new FileNotFoundException("未能在jar包中找到文件：" + src);
            }
            URLConnection urlConnection = url.openConnection();

            if (!descIsFilename && !dest.endsWith("/")) {
                dest = dest + "/";
            }
            if (urlConnection instanceof FileURLConnection) {
                copyFileResources(src, dest, clazz, descIsFilename);
            } else if (urlConnection instanceof JarURLConnection) {
                copyJarResources((JarURLConnection) urlConnection, dest, clazz, descIsFilename);
            } else {
                throw new IllegalStateException("既不是FileURLConnection，也不是JarURLConnection");
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static void copy(String src, String dest, Class clazz) {
        copy(src, dest, clazz, false);
    }


    public static void copy(String src, String dest, boolean descIsFilename) {
        copy(src, dest, JarFileUtil.class, descIsFilename);
    }

    public static void copy(String src, String dest) {
        copy(src, dest, JarFileUtil.class, false);
    }


    /**
     * 当前运行环境资源文件是在jar里面的
     *
     * @param jarUrlConnection
     * @throws IOException
     */
    private static void copyJarResources(JarURLConnection jarUrlConnection, String destFolderPath, Class clazz, boolean descIsFilename) {
        try (JarFile jarFile = jarUrlConnection.getJarFile()) {
            String entryName = jarUrlConnection.getEntryName();
            String basePath = "/" + entryName;


            // is directory
            if (entryName.endsWith("/")) {
                basePath = basePath.substring(0, basePath.lastIndexOf("/", basePath.length() - 2) + 1);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(entryName)) {
                        copyJarResources("/" + entry.getName(), destFolderPath, clazz, basePath, false);
                    }
                }
            } else {
                basePath = basePath.substring(0, basePath.lastIndexOf("/"));
                copyJarResources("/" + entryName, destFolderPath, clazz, basePath, descIsFilename);
            }
        } catch (IOException e) {
            LambdaUtil.raise(e);
        }
    }

    /**
     * 复制jar包中的文件
     *
     * @param src            jar包中的文件路径，只支持绝对路径, 如果需要复制整个文件夹，则路径必须以 / 结尾
     * @param dest           目标路径
     * @param clazz          类
     * @param descIsFilename 目标路径作为文件路径
     * @throws IOException
     */
    private static void copyJarResources(String src, String dest, Class clazz, String basePath, boolean descIsFilename) {
        if (!src.startsWith("/")) {
            throw new IllegalArgumentException("The src has to be absolute (start with '/').");
        }

        // 以/结尾，是个文件夹
        if (src.endsWith("/")) {
            FileUtil.mkdir(dest + src.substring(basePath.length()));
            return;
        }

        // 创建文件, 如果设置了目标路径作为文件路径，那么就直接用给定的文件名而不用jar中的文件名，也就是复制并重命名功能
        File destFile;
        if (descIsFilename) {
            destFile = FileUtil.touch(dest);
        } else {
            destFile = FileUtil.touch(dest + src.substring(basePath.length()));
        }

        // 复制到文件
        try (InputStream in = clazz.getResourceAsStream(src)) {
            if (in == null) {
                throw new FileNotFoundException("File " + src + " was not found inside JAR.");
            }
            FileUtil.writeFromStream(in, destFile);
        } catch (IOException e) {
            LambdaUtil.raise(e);
        }
    }


    private static void copyFileResources(String src, String dest, Class clazz, boolean descIsFilename) {
        String basePath = clazz.getResource("/").getPath();
        if (descIsFilename) {
            FileUtil.mkParentDirs(dest);
        } else {
            FileUtil.mkdir(dest);
        }

        FileUtil.copy(basePath + src, dest, true);
    }


}
