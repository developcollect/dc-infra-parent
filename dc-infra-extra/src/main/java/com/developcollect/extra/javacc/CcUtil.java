package com.developcollect.extra.javacc;


import cn.hutool.core.util.StrUtil;
import com.developcollect.core.utils.FileUtil;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.io.File;
import java.io.IOException;

/**
 * call chain util
 */
public class CcUtil {

    /**
     * 传入一个Maven项目，解析该项目中的类中的方法调用关系
     * @param mavenProjectDir maven项目文件夹
     * @return
     */
    public Object parseChain(String mavenProjectDir) {
        // 解析maven项目中的模块

        // 执行clear、compile命令，定位classes目录
        // 识别出依赖的jar包
        // 定位需要解析的类和方法，执行解析
        return null;
    }











}
