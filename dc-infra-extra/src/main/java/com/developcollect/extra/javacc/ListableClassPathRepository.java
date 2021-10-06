package com.developcollect.extra.javacc;

import com.developcollect.core.utils.ArrayUtil;
import com.developcollect.core.utils.FileUtil;
import com.developcollect.core.utils.LambdaUtil;
import com.developcollect.core.utils.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.MemorySensitiveClassPathRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


@Slf4j
public class ListableClassPathRepository extends MemorySensitiveClassPathRepository implements ListableRepository {

    /**
     *
     */
    private final PathEntryWrapper[] paths;

    public ListableClassPathRepository(String[] paths) {
        this(new ClassPath(ArrayUtil.join(paths, ":")));
    }

    public ListableClassPathRepository(ClassPath classPath) {
        super(classPath);
        Object[] cps = (Object[]) ReflectUtil.getFieldValue(classPath, "paths");

        ArrayList<PathEntryWrapper> tmpList = new ArrayList<>(cps.length);
        for (Object cp : cps) {
            String csName = cp.getClass().getSimpleName();
            if ("Jar".equals(csName)) {
                tmpList.add(PathEntryWrapper.ofJar(cp));
            } else if ("Dir".equals(csName)) {
                tmpList.add(PathEntryWrapper.ofDir(cp));
            }
        }
        paths = tmpList.toArray(new PathEntryWrapper[0]);
    }

    @Override
    public void forEachClass(Consumer<JavaClass> action) {
        for (PathEntryWrapper path : paths) {
            if (path.isDir()) {
                listClassesDir(path, file -> file.getName().endsWith(".class") && file.isFile(), action);
            } else if (path.isJar()) {
                listJar(path, je -> !je.isDirectory() && je.getName().endsWith(".class"), action);
            }
        }
    }


    /**
     * 获取接口的子类
     *
     * @param inter 接口
     * @throws ClassNotFoundException
     */
    @Override
    public List<JavaClass> getImplementationOf(JavaClass inter) throws ClassNotFoundException {
        return scanClasses(jc -> {
            try {
                return jc.implementationOf(inter);
            } catch (ClassNotFoundException e) {
                LambdaUtil.raise(e);
            }
            return false;
        });
    }

    @Override
    public List<JavaClass> getInstanceOf(JavaClass superClass) throws ClassNotFoundException {
        return scanClasses(jc -> {
            try {
                return jc.instanceOf(superClass);
            } catch (ClassNotFoundException e) {
                LambdaUtil.raise(e);
            }
            return false;
        });
    }

    @Override
    public List<JavaClass> getSubClassList(JavaClass superClass) throws ClassNotFoundException {
        if (superClass.isInterface()) {
            return scanClasses(jc -> {
                // 排除自己
                if (jc.equals(superClass)) {
                    return false;
                }
                try {
                    return jc.implementationOf(superClass);
                } catch (ClassNotFoundException e) {
                    LambdaUtil.raise(e);
                }
                return false;
            });
        } else {
            return scanClasses(jc -> {
                // 排除自己
                if (jc.equals(superClass)) {
                    return false;
                }
                try {
                    return jc.instanceOf(superClass);
                } catch (ClassNotFoundException e) {
                    LambdaUtil.raise(e);
                }
                return false;
            });
        }
    }

    @Override
    public List<JavaClass> scanClasses(Predicate<JavaClass> filter) {
        List<JavaClass> javaClasses = new ArrayList<>();
        forEachClass(jc -> {
            if (filter.test(jc)) {
                javaClasses.add(jc);
            }
        });
        return javaClasses;
    }

    @Override
    public List<ClassAndMethod> scanMethods(Predicate<ClassAndMethod> filter) {
        List<ClassAndMethod> classAndMethods = new ArrayList<>();
        forEachClass(jc -> {
            Method[] methods = jc.getMethods();
            for (Method method : methods) {
                ClassAndMethod classAndMethod = new ClassAndMethod(jc, method);
                if (filter.test(classAndMethod)) {
                    classAndMethods.add(classAndMethod);
                }
            }
        });
        return classAndMethods;
    }


    private void listClassesDir(PathEntryWrapper path, Predicate<File> filter, Consumer<JavaClass> consumer) {
        FileUtil.walkFiles(path.dir, file -> {
            if (!filter.test(file)) {
                return;
            }

            try {
                ClassParser classParser = new ClassParser(file.getAbsolutePath());
                JavaClass javaClass = classParser.parse();
                storeClass(javaClass);
                consumer.accept(javaClass);
            } catch (IOException e) {
                log.error("parse class error: {}", file.getAbsolutePath(), e);
            }
        });
    }

    private void listJar(PathEntryWrapper path, Predicate<ZipEntry> filter, Consumer<JavaClass> consumer) {
        try (ZipFile jar = path.zipFile) {
            for (Enumeration<? extends ZipEntry> enumeration = jar.entries(); enumeration.hasMoreElements(); ) {
                ZipEntry jarEntry = enumeration.nextElement();
                if (!filter.test(jarEntry)) {
                    return;
                }

                try {
                    ClassParser classParser = new ClassParser(jar.getName(), jarEntry.getName());
                    JavaClass javaClass = classParser.parse();
                    storeClass(javaClass);
                    consumer.accept(javaClass);
                } catch (IOException e) {
                    log.error("parse class error: {}", jarEntry.getName(), e);
                }
            }
        } catch (IOException e) {
            log.error("walk jar error: {}", path.zipFile.getName(), e);
        }
    }


    static class PathEntryWrapper {
        private Object source;
        /**
         * 1 dir
         * 2 jar
         */
        private int type;

        String dir;
        ZipFile zipFile;

        public boolean isJar() {
            return type == 2;
        }

        public boolean isDir() {
            return type == 1;
        }

        static PathEntryWrapper ofDir(Object source) {
            PathEntryWrapper wrapper = new PathEntryWrapper();
            wrapper.source = source;
            wrapper.type = 1;
            wrapper.dir = (String) ReflectUtil.getFieldValue(source, "dir");
            ;
            return wrapper;
        }

        static PathEntryWrapper ofJar(Object source) {
            PathEntryWrapper wrapper = new PathEntryWrapper();
            wrapper.source = source;
            wrapper.type = 2;
            wrapper.zipFile = (ZipFile) ReflectUtil.getFieldValue(source, "zipFile");
            return wrapper;
        }
    }
}
