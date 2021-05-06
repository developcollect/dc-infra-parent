package com.developcollect.core.json;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.developcollect.core.utils.LambdaUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;


public class JSONUtil extends cn.hutool.json.JSONUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJsonStr(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return LambdaUtil.raise(e);
        }
    }

    public static <T> T parse(String jsonStr, Class<T> clazz) {
        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            return LambdaUtil.raise(e);
        }
    }

    public static <T> List<T> parseList(String jsonStr, Class<T> clazz) {
        try {
            return mapper.readValue(jsonStr, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            return LambdaUtil.raise(e);
        }
    }

    public static <T> T parse(String jsonStr, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(jsonStr, typeReference);
        } catch (JsonProcessingException e) {
            return LambdaUtil.raise(e);
        }
    }


}
