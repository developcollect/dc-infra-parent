package com.developcollect.utils;


import cn.hutool.core.io.IoUtil;
import org.slf4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件下载
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/12 9:18
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
public class DownloadUtil {


    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DownloadUtil.class);

    /**
     * see {@link #download(String, String, Transporter, boolean)}
     *
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/7/12 9:48
     */
    public static File download(String urlStr, String savePath, boolean overwrite) {
        File file = new File(savePath + File.separator + getFilenameFromUrl(urlStr));
        return download(urlStr, file, overwrite);
    }

    /**
     * 从指定的链接中下载一个文件，保存在指定位置
     *
     * @param urlStr    链接
     * @param saveFile  保存位置
     * @param overwrite 是否覆盖原文件
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2020/10/20 15:13
     */
    public static File download(String urlStr, File saveFile, boolean overwrite) {
        return download(urlStr, saveFile, IoUtil::copy, overwrite);
    }

    /**
     * 从url中下载文件到指定的路径
     *
     * @param urlStr      url
     * @param savePath    文件保存路径
     * @param transporter 传输器  可用于指定具体的传输过程
     * @param overwrite   是否覆盖原文件
     * @return java.io.File 已下载完成的文件
     * @author Zhu Kaixiao
     * @date 2019/7/12 9:46
     */
    public static File download(String urlStr, String savePath, Transporter transporter, boolean overwrite) {
        return download(urlStr, new File(savePath), transporter, overwrite);
    }

    /**
     * 下载文件到指定的位置
     *
     * @param urlStr
     * @param file
     * @param transporter
     * @param overwrite
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2020/10/20 15:14
     */
    public static File download(String urlStr, File file, Transporter transporter, boolean overwrite) {
        try {
            if (!overwrite && file.exists()) {
                throw new IllegalArgumentException(String.format("文件已存在: [%s]", file.getCanonicalPath()));
            }

            // 创建文件
            FileUtil.touch(file);


            URL url = new URL(encodeUrl(urlStr));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            conn.setRequestProperty("Host", url.getHost());
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");


            try (InputStream inputStream = conn.getInputStream();
                 FileOutputStream fos = new FileOutputStream(file)) {
                // 用指定的流传输器传输流
                transporter.transport(inputStream, fos);
                fos.flush();
            }

            log.debug("{} download success", urlStr);
            return file;
        } catch (IOException e) {
            log.warn("{} download fail", urlStr);
            throw new RuntimeException(e);
        }
    }


    public static File download(String urlStr, File saveFile) {
        return download(urlStr, saveFile, true);
    }

    /**
     * 从指定url中下载文件   文件会被保存在系统临时目录
     *
     * @param urlStr
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 14:52
     */
    public static File download(String urlStr) {
        File tempFile = FileUtil.createTempFile();
        download(urlStr, tempFile, true);
        File file = FileUtil.renameToRealType(tempFile);
        return file;
    }


    public static File downloadAndFixFilename(String urlStr, String saveDir) {
        File downloadFile = download(urlStr, saveDir);
        File file = FileUtil.renameToRealType(downloadFile);
        return file;
    }

    /**
     * 下载文件到指定目录, 文件名将自动创建
     *
     * @param urlStr
     * @param saveDir
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 15:07
     */
    public static File download(String urlStr, String saveDir) {
        return download(urlStr, saveDir, true);
    }


    /**
     * 下载文件到指定目录
     *
     * @param urlStr   下载地址
     * @param saveDir  保存目录
     * @param filename 文件名
     */
    public static File download(String urlStr, String saveDir, String filename) {
        File file = new File(saveDir + File.separator + filename);
        download(urlStr, file);
        return file;
    }


    @FunctionalInterface
    public interface Transporter {

        /**
         * 从输入流传送到输出流
         *
         * @param in  输入流
         * @param out 输出流
         */
        void transport(InputStream in, OutputStream out);
    }


    /**
     * url编码   避免url中有中文时报错
     *
     * @param urlStr url
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/8/5 14:57
     **/
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    private static String encodeUrl(String urlStr) throws UnsupportedEncodingException {
        StringBuilder newUrlSb = new StringBuilder();
        String protocol = StrUtil.subBefore(urlStr, "://", false);
        newUrlSb.append(protocol).append("://");
        String urlBody = StrUtil.subAfter(urlStr, "://", false);

        String hf = urlBody;
        String param = null;
        if (urlBody.contains("?")) {
            // host and file
            hf = StrUtil.subBefore(urlBody, "?", false);
            // parameter
            param = StrUtil.subAfter(urlBody, "?", false);
        }

        String[] sfSp = hf.split("/+");
        for (int i = 0; i < sfSp.length; i++) {
            // host, may be have port
            if (i == 0) {
                newUrlSb.append(sfSp[i]).append("/");
            } else {
                newUrlSb.append(URLUtil.encode(sfSp[i])).append("/");
            }
        }

        newUrlSb.delete(newUrlSb.length() - 1, newUrlSb.length());

        if (param != null) {
            newUrlSb.append("?");
            for (String s : param.split("&")) {
                String[] paramSp = s.split("=");
                newUrlSb.append(URLUtil.encode(paramSp[0])).append("=").append(URLUtil.encode(paramSp[1])).append("&");
            }
            newUrlSb.delete(newUrlSb.length() - 1, newUrlSb.length());
        }

        return newUrlSb.toString();
    }

    private static String getFilenameFromUrl(String urlStr) {
        String substring = urlStr.substring(urlStr.lastIndexOf("/") + 1);

        int idx = substring.indexOf("?");
        if (idx > 0) {
            substring = substring.substring(0, idx);
        }
        return substring;
    }
}
