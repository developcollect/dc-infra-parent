package com.developcollect.extra.javacc;

import com.developcollect.extra.maven.MavenUtil;
import com.developcollect.extra.maven.ProjectStructure;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class CcUtilTest {



    @Test
    public void test2a() {
        parseChain("/Volumes/D2/code/java-projects/dc-infra-parent", cm -> true);
    }

    @Test
    public void test2b() {
        Map<ClassAndMethod, CallInfo> chainMap = parseChain("/Volumes/D2/code/java-projects/first", cm -> {
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

        CcSupport.printChainMap(chainMap);
    }


    @Test
    public void testf1() {
        Map<ClassAndMethod, CallInfo> chainMap = parseChain("/Volumes/D2/code/java-projects/first", cm -> {
            JavaClass javaClass = cm.getJavaClass();
            Method method = cm.getMethod();
            return javaClass.getClassName().equals("org.example.TestEntry") && method.getName().equals("f1");
        });

        CcSupport.printChainMap(chainMap);
    }


    @Test
    public void test_TestEntry() {
        Map<ClassAndMethod, CallInfo> chainMap = parseChain("/Volumes/D2/code/java-projects/first", cm -> {
            JavaClass javaClass = cm.getJavaClass();
            return javaClass.getClassName().equals("org.example.TestEntry");
        });

        CcSupport.printChainMap(chainMap);
    }


    @Test
    public void testf33() {
        Map<ClassAndMethod, CallInfo> chainMap = parseChain("/Volumes/D2/code/java-projects/first", cm -> {
            JavaClass javaClass = cm.getJavaClass();
            Method method = cm.getMethod();
            return javaClass.getClassName().equals("org.example.TestEntry") && method.getName().equals("f33");
        }, callInfo -> {
            CallInfo.Call caller = callInfo.getCaller();
            MethodInfo methodInfo = caller.getMethodInfo();
            String callerClassName = methodInfo.getClassName();
            return callerClassName.startsWith("org.example");
        });

        CcSupport.printChainMap(chainMap);
    }


    public static Map<ClassAndMethod, CallInfo> parseChain(String mavenProjectDir, Predicate<ClassAndMethod> scanFilter) {
        return parseChain(mavenProjectDir, scanFilter, null);
    }


    /**
     * 传入一个Maven项目，解析该项目中的类中的方法调用关系
     *
     * @param mavenProjectDir maven项目文件夹
     * @return
     */
    public static Map<ClassAndMethod, CallInfo> parseChain(String mavenProjectDir, Predicate<ClassAndMethod> scanFilter, Predicate<CallInfo> parseFilter) {
        // 解析maven项目结构
        ProjectStructure projectStructure = MavenUtil.analysisProject(mavenProjectDir);

        // 执行clear、compile命令，定位classes目录
        MavenUtil.mvnWithThrow(projectStructure.getPomPath(), "clean", "compile");

        // 定位classes目录
        List<String> classPaths = MavenUtil.collectClassPaths(projectStructure);

        return CcUtil.parseChain(classPaths, scanFilter, parseFilter, null);
    }
}