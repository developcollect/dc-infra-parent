//package com.developcollect.exception;
//
//
//import java.lang.reflect.Field;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//
///**
// * 异常辅助工具
// * 可以手动触发类加载，以实现初始化相关map，并执行异常状态码重复校验
// *
// * @author zak
// * @version 1.0
// * @date 2020/10/19 11:21
// */
//class ExceptionHelper {
//
//    /**
//     * code名称 ==> code
//     */
//    private static final Map<String, Integer> exceptionCodeMap;
//
//    /**
//     * code ==> message
//     */
//    private static final Map<Integer, String> exceptionCodeMessageMap;
//
//    static {
//        Map<String, Integer> tmp = new HashMap<>();
//        Map<Integer, String> tmp2 = new HashMap<>();
//        Field[] fields = Optional.ofNullable(Exceptions.class.getFields()).orElse(new Field[0]);
//
//        for (Field field : fields) {
//            try {
//                Object o = field.get(null);
//                if (o instanceof IException) {
//                    IException e = (IException) o;
//                    if (tmp2.containsKey(e.getCode())) {
//                        throw new IllegalStateException("异常状态码重复: " + e.getCode());
//                    }
//                    tmp.put(field.getName() + "_CODE", e.getCode());
//                    tmp2.put(e.getCode(), e.getMessage());
//                }
//            } catch (IllegalAccessException ignore) {
//            }
//        }
//
//        exceptionCodeMap = Collections.unmodifiableMap(tmp);
//        exceptionCodeMessageMap = Collections.unmodifiableMap(tmp2);
//    }
//
//    static int getCode(String codeKey) {
//        return exceptionCodeMap.get(codeKey);
//    }
//
//    static String getMessage(int code) {
//        return exceptionCodeMessageMap.get(code);
//    }
//
//    static Map<String, Integer> getCodeMap() {
//        return exceptionCodeMap;
//    }
//}
