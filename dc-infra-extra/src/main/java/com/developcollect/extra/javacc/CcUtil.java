package com.developcollect.extra.javacc;


import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
        Map<ClassAndMethod, CallInfo> result = new HashMap<>(classAndMethods.size());
        int size = classAndMethods.size();
        int count = 0;
        for (ClassAndMethod cm : classAndMethods) {
            if (result.containsKey(cm)) {
                if (log.isDebugEnabled()) {
                    log.debug("[{}/{}] 解析 {} 已存在解析结果，跳过。", ++count, size, cm);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("[{}/{}] 解析 {}", ++count, size, cm);
                }
                result.put(cm, parser.parse(cm.getJavaClass(), cm.getMethod()));
            }
            if (count == 3) {
                break;
            }
        }

        return result;
    }


}
