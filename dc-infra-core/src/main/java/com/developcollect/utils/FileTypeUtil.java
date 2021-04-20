package com.developcollect.utils;

import cn.hutool.core.io.IORuntimeException;

import java.io.InputStream;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/2/6 15:01
 */
public class FileTypeUtil extends cn.hutool.core.io.FileTypeUtil {


    /**
     * 根据输入流和原始扩展名识别文件类型
     *
     * @param in      输入流
     * @param extName 原始扩展名
     * @return
     * @throws IORuntimeException
     */
    public static String getType(InputStream in, String extName) throws IORuntimeException {
        String typeName = getType(in);

        if (null == typeName) {
            // 未成功识别类型，扩展名辅助识别
            typeName = extName;
        } else if ("xls".equals(typeName)) {
            // xls、doc、msi的头一样，使用扩展名辅助判断
            if ("doc".equalsIgnoreCase(extName)) {
                typeName = "doc";
            } else if ("msi".equalsIgnoreCase(extName)) {
                typeName = "msi";
            }
        } else if ("zip".equals(typeName)) {
            // zip可能为docx、xlsx、pptx、jar、war等格式，扩展名辅助判断
            if ("docx".equalsIgnoreCase(extName)) {
                typeName = "docx";
            } else if ("xlsx".equalsIgnoreCase(extName)) {
                typeName = "xlsx";
            } else if ("pptx".equalsIgnoreCase(extName)) {
                typeName = "pptx";
            } else if ("jar".equalsIgnoreCase(extName)) {
                typeName = "jar";
            } else if ("war".equalsIgnoreCase(extName)) {
                typeName = "war";
            }
        }
        return typeName;
    }
}
