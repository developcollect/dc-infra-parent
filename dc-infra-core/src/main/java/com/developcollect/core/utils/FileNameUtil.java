package com.developcollect.core.utils;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.StrUtil;

import java.util.Set;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/11/16 15:16
 */
public class FileNameUtil extends cn.hutool.core.io.file.FileNameUtil {


    /**
     * img的后缀
     */
    public static final Set<String> IMG_EXT = CollectionUtil.newHashSet("jpg", "jpeg", "png", "gif", "bmp", "ico");
    /**
     * doc的后缀
     */
    public static final Set<String> DOC_EXT = CollectionUtil.newHashSet("doc", "docx", "wps", "txt", "pdf");
    /**
     * excel后缀
     */
    public static final Set<String> EXCEL_EXT = CollectionUtil.newHashSet("xlsx", "xlsm", "xltx", "xltm", "xlsb", "xlam");
    /**
     * ppt后缀
     */
    public static final Set<String> PPT_EXT = CollectionUtil.newHashSet("ppt", "pptx", "pptm", "ppsx", "potx", "potm");
    /**
     * 视频后缀
     */
    public static final Set<String> VIDEO_EXT = CollectionUtil.newHashSet("avi", "asf", "wmv", "avs", "flv", "mkv", "mov", "3gp",
            "mp4", "mpg", "mpeg", "dat", "ogm", "vob", "rmvb", "rm", "ts", "ifo");
    /**
     * 音频后缀
     */
    public static final Set<String> AUDIO_EXT = CollectionUtil.newHashSet("wav", "aac", "mp3", "aif", "au", "ram", "wma", "amr");
    /**
     * 压缩包后缀
     */
    public static final Set<String> COMPRESS_EXT = CollectionUtil.newHashSet("zip", "rar", "tar", "gz", "tar.gz", "7z", "gzip");


    /**
     * 替换原文件名中的后缀名，如果原文件没有后缀名，则添加后缀名
     * 例子：
     * a.txt      ==>  a.exe
     * a.txt.mp5  ==>  a.txt.exe
     * .jcg       ==>  .jcg.exe
     * data       ==>  data.exe
     *
     * @param originFileName
     * @param suffix
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/11/16 15:18
     */
    public static String replaceSuffix(String originFileName, String suffix) {

        // 扩展名中不能包含路径相关的符号
        if (StrUtil.containsAny(suffix, UNIX_SEPARATOR, WINDOWS_SEPARATOR)) {
            throw new UtilException("扩展名中不能包含路径相关的符号: " + UNIX_SEPARATOR + "、" + WINDOWS_SEPARATOR);
        }
        int dotIdx = originFileName.lastIndexOf(StrUtil.DOT);

        return dotIdx < 1
                ? originFileName + StrUtil.DOT + suffix
                : originFileName.substring(0, dotIdx + 1) + suffix;
    }


    /**
     * 是否是文档
     *
     * @param ext 后缀格式
     */
    public static boolean isDoc(String ext) {
        return DOC_EXT.contains(ext.toLowerCase());
    }

    /**
     * 是否是图片
     *
     * @param ext 后缀格式
     */
    public static boolean isImg(String ext) {
        return IMG_EXT.contains(ext.toLowerCase());
    }

    /**
     * 是否Excel格式
     *
     * @param ext 格式
     * @Title: isValidExcelExt
     * @return: boolean true 是Excel
     */
    public static boolean isExcel(String ext) {
        return EXCEL_EXT.contains(ext.toLowerCase());
    }

    /**
     * 是否ppt格式
     *
     * @param ext 格式
     * @Title: isValidPptExt
     * @return: boolean true 是ppt
     */
    public static boolean isPpt(String ext) {
        return PPT_EXT.contains(ext.toLowerCase());
    }

    /**
     * 是否视频格式
     *
     * @param ext 格式
     * @Title: isValidVideoExt
     * @return: boolean true 是视频
     */
    public static boolean isVideo(String ext) {
        return VIDEO_EXT.contains(ext.toLowerCase());
    }

    /**
     * 是否音频格式
     *
     * @param ext 格式
     * @Title: isValidAudioExt
     * @return: boolean true 是音频
     */
    public static boolean isAudio(String ext) {
        return AUDIO_EXT.contains(ext.toLowerCase());
    }

    /**
     * 是否压缩包格式
     *
     * @param ext 格式
     * @Title: isValidZipExt
     * @return: boolean true 是压缩包
     */
    public static boolean isCompress(String ext) {
        return COMPRESS_EXT.contains(ext.toLowerCase());
    }


}
