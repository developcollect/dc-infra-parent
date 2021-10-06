package com.developcollect.extra.javacc;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPathRepository;
import org.apache.bcel.util.Repository;

import java.util.Arrays;

public class ClassPathsRepository implements Repository {

    private ClassPathRepository[] repositories;

    public ClassPathsRepository(String[] paths) {
        this(Arrays.stream(paths)
                .map(ClassPath::new)
                .toArray(ClassPath[]::new)
        );
    }

    public ClassPathsRepository(ClassPath[] paths) {
        repositories = new ClassPathRepository[paths.length];
        for (int i = 0; i < paths.length; i++) {
            repositories[i] = new ClassPathRepository(paths[i]);
        }
    }

    @Override
    public void storeClass(JavaClass clazz) {

    }

    @Override
    public void removeClass(JavaClass clazz) {

    }

    @Override
    public JavaClass findClass(String className) {
        for (ClassPathRepository repository : repositories) {
            JavaClass javaClass = repository.findClass(className);
            if (javaClass != null) {
                return javaClass;
            }
        }
        return null;
    }

    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException {
        for (ClassPathRepository repository : repositories) {
            try {
                return repository.loadClass(className);
            } catch (ClassNotFoundException ignore) {
            }
        }
        throw new ClassNotFoundException("Exception while looking for class " + className);
    }

    @Override
    public JavaClass loadClass(Class<?> clazz) throws ClassNotFoundException {
        for (ClassPathRepository repository : repositories) {
            try {
                return repository.loadClass(clazz);
            } catch (ClassNotFoundException ignore) {
            }
        }
        throw new ClassNotFoundException("Exception while looking for class " + clazz);
    }

    @Override
    public void clear() {
        for (ClassPathRepository repository : repositories) {
            repository.clear();
        }
    }

    @Override
    public ClassPath getClassPath() {
        return null;
    }
}
