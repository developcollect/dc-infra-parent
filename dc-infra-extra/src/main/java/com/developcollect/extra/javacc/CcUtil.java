package com.developcollect.extra.javacc;


import cn.hutool.core.exceptions.UtilException;
import com.developcollect.core.tree.TreeUtil;
import com.developcollect.extra.maven.MavenUtil;
import com.developcollect.extra.maven.ProjectStructure;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.InvocationResult;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * call chain util
 */
@Slf4j
public class CcUtil {


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
        InvocationResult invocationResult = MavenUtil.mvnWithThrow(projectStructure.getPomPath(), "clean", "compile");

        if (invocationResult.getExitCode() != 0) {
            throw new UtilException("执行【mvn clear compile】命令失败");
        }

        // 定位classes目录
        String[] classPaths = collectClassPaths(projectStructure);

        // 扫描类，定位需要解析的类和方法
        BcelClassLoader bcelClassLoader = new BcelClassLoader(classPaths);
        List<ClassAndMethod> classAndMethods = bcelClassLoader.scanMethods(scanFilter);

        // 执行解析
        CallChainParser parser = new CallChainParser(bcelClassLoader);
        if (parseFilter != null) {
            parser.addFilter(parseFilter);
        }
        Map<ClassAndMethod, CallInfo> result = classAndMethods.stream()
                .collect(Collectors.toMap(cm -> cm, cm -> parser.parse(cm.getJavaClass(), cm.getMethod())));

        return result;
    }


    private static String[] collectClassPaths(ProjectStructure projectStructure) {
        List<ProjectStructure> projectStructures = TreeUtil.flat(projectStructure, TreeUtil::preOrder, ps -> !"pom".equals(ps.getPackaging()));
        String[] classPaths = projectStructures.stream()
                .map(ps -> ps.getProjectPath() + "/target/classes")
                .toArray(String[]::new);
        return classPaths;
    }












}
