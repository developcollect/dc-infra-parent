package com.developcollect.extra.javacc;


import com.developcollect.extra.maven.MavenUtil;
import com.developcollect.extra.maven.ProjectStructure;
import lombok.extern.slf4j.Slf4j;

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
        MavenUtil.mvnWithThrow(projectStructure.getPomPath(), "clean", "compile");

        // 定位classes目录
        String[] classPaths = MavenUtil.collectClassPaths(projectStructure);

        return parseChain(classPaths, scanFilter, parseFilter, null);
    }

    public static Map<ClassAndMethod, CallInfo> parseChain(String[] paths, Predicate<ClassAndMethod> scanFilter, Predicate<CallInfo> parseFilter, CallChainParser.SubClassScanner subClassScanner) {
        // 扫描类，定位需要解析的类和方法
        ListableClassPathRepository repository = new ListableClassPathRepository(paths);
        List<ClassAndMethod> classAndMethods = repository.scanMethods(scanFilter);

        // 创建解析器
        CallChainParser parser = new CallChainParser(repository);
        if (parseFilter != null) {
            parser.addParseFilter(parseFilter);
        }
        if (subClassScanner != null) {
            parser.setSubClassScanner(subClassScanner);
        }

        // 执行解析
        Map<ClassAndMethod, CallInfo> result = classAndMethods.stream()
                .collect(Collectors.toMap(cm -> cm, cm -> parser.parse(cm.getJavaClass(), cm.getMethod())));

        return result;
    }


}
