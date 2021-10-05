package com.developcollect.extra.javacc;

import com.developcollect.core.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class BcelClassLoader {

    /**
     * classPath，支持文件夹和jar包两个形式
     */
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


    public List<ClassAndMethod> scanMethods(Predicate<ClassAndMethod> filter) {
        List<ClassAndMethod> classAndMethods = new ArrayList<>();
        for (String classPath : classPaths) {
            File f = new File(classPath);
            if (!f.exists()) {
                log.error("class path not exists: {}", classPath);
                continue;
            }

            // 文件夹，直接遍历class文件
            if (f.isDirectory()) {
                FileUtil.walkFiles(classPath, file -> {
                    // 忽略资源文件
                    if (!file.getName().endsWith(".class") || !file.isFile()) {
                        return;
                    }

                    try {
                        ClassParser classParser = new ClassParser(file.getAbsolutePath());
                        JavaClass javaClass = classParser.parse();
                        Method[] methods = javaClass.getMethods();
                        for (Method method : methods) {
                            ClassAndMethod classAndMethod = new ClassAndMethod(javaClass, method);
                            if (filter.test(classAndMethod)) {
                                classAndMethods.add(classAndMethod);
                            }
                        }
                    } catch (IOException e) {
                        log.error("parse class error: {}", file.getAbsolutePath(), e);
                    }
                });
            }
            // 否则就当做jar包遍历
            else {
                try (JarFile jar = new JarFile(f)) {
                    for (Enumeration<JarEntry> enumeration = jar.entries(); enumeration.hasMoreElements(); ) {
                        JarEntry jarEntry = enumeration.nextElement();
                        // 忽略资源文件
                        if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                            continue;
                        }

                        try {
                            ClassParser classParser = new ClassParser(classPath, jarEntry.getName());
                            JavaClass javaClass = classParser.parse();
                            Method[] methods = javaClass.getMethods();
                            for (Method method : methods) {
                                ClassAndMethod classAndMethod = new ClassAndMethod(javaClass, method);
                                if (filter.test(classAndMethod)) {
                                    classAndMethods.add(classAndMethod);
                                }
                            }
                        } catch (IOException e) {
                            log.error("parse class error: {}", jarEntry.getName(), e);
                        }
                    }
                } catch (IOException e) {
                    log.error("walk jar error: {}", f.getAbsolutePath(), e);
                }
            }
        }
        return classAndMethods;
    }


}
