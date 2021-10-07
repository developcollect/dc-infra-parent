package com.developcollect.extra.javacc;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

public interface ICallChainParser {

    CallInfo parse(String className, String methodName, Type... argTypes);

    CallInfo parse(JavaClass javaClass, Method method);

}
