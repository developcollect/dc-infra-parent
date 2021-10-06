package com.developcollect.extra.javacc;

import org.apache.bcel.classfile.JavaClass;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;


public interface ListableRepository {

    void forEachClass(Consumer<JavaClass> action);

    List<JavaClass> scanClasses(Predicate<JavaClass> filter);

    List<ClassAndMethod> scanMethods(Predicate<ClassAndMethod> filter);
}
