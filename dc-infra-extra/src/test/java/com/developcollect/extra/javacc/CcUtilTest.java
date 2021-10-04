package com.developcollect.extra.javacc;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.junit.Test;

import java.util.Map;

public class CcUtilTest {


    @Test
    public void test2a() {
        CcUtil.parseChain("/Volumes/D2/code/java-projects/dc-infra-parent", cm -> true);
    }

    @Test
    public void test2b() {
        Map<ClassAndMethod, CallInfo> chainMap = CcUtil.parseChain("/Volumes/D2/code/java-projects/first", cm -> {
            JavaClass javaClass = cm.getJavaClass();
            Method method = cm.getMethod();
            if (javaClass.getClassName().equals("org.example.TestEntry") && method.getName().equals("en2")) {
                Type[] argumentTypes = method.getArgumentTypes();
                if (argumentTypes.length == 1 && argumentTypes[0].equals(Type.getType(String.class))) {
                    return true;
                }
            }
            return false;
        });

        for (Map.Entry<ClassAndMethod, CallInfo> entry : chainMap.entrySet()) {
            System.out.println(entry.getKey());
            CcInnerUtil.printCallInfo(entry.getValue());
            System.out.println("\n\n");
        }
    }

}