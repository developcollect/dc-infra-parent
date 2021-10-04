package com.developcollect.extra.javacc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {

    private String className;

    private String methodName;

    private Type[] argumentTypes;


    public String getMethodSignature() {
        return CcInnerUtil.getMethodSig(className, methodName, argumentTypes);
    }

    public static MethodInfo of(JavaClass javaClass, Method method) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setClassName(javaClass.getClassName());
        methodInfo.setMethodName(method.getName());
        methodInfo.setArgumentTypes(method.getArgumentTypes());
        return methodInfo;
    }

    @Override
    public String toString() {
        return getMethodSignature();
    }
}
