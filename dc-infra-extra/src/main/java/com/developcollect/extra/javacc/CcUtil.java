package com.developcollect.extra.javacc;


import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * call chain util
 */
@Slf4j
public class CcUtil {


    public static Map<ClassAndMethod, CallInfo> parseChain(Collection<String> paths, Predicate<ClassAndMethod> scanFilter, Predicate<CallInfo> parseFilter, CallChainParser.SubClassScanner subClassScanner) {
        // 扫描类，定位需要解析的类和方法
        ListableClassPathRepository repository = new ListableClassPathRepository(paths.toArray(new String[0]));
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
