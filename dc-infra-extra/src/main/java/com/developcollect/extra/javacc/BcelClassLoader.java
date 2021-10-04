package com.developcollect.extra.javacc;

import com.developcollect.core.utils.FileUtil;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.IOException;

public class BcelClassLoader {

    private String[] classPaths;

    public BcelClassLoader(String[] classPaths) {
        if (classPaths == null) {
            throw new IllegalArgumentException("classPaths can't be null");
        }
        this.classPaths = classPaths;
    }


    public JavaClass getJavaClass(String className) {
        for (String classPath : classPaths) {
            if (FileUtil.isDirectory(classPath)) {
                try {
                    return getJavaClassFromDir(classPath, className);
                } catch (IOException ignore) {
                }
            } else {
                try {
                    return getJavaClassFromZipFile(classPath, className);
                } catch (IOException ignore) {
                }
            }
        }

        throw new RuntimeException("无法加载类文件：" + className);
    }


    private JavaClass getJavaClassFromZipFile(String zipFile, String className) throws IOException {
        String classFileName = CcInnerUtil.fixClassFileName(className);
        ClassParser classParser = new ClassParser(zipFile, classFileName);
        return classParser.parse();
    }

    private JavaClass getJavaClassFromDir(String dir, String className) throws IOException {
        String classFileName = CcInnerUtil.fixClassFileName(className);
        ClassParser classParser = new ClassParser(FileUtil.getCanonicalPath(new File(dir + "/" + classFileName)));
        return classParser.parse();
    }




}
