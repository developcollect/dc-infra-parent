package com.developcollect.core.utils.miaotixing;

import cn.hutool.http.HttpUtil;
import com.developcollect.core.json.JSONUtil;
import com.developcollect.core.utils.BeanUtil;

import java.util.Map;

/**
 * 喵提醒工具类
 */
public class MiaoTiXingUtil {

    private static final String TRIGGER_URL = "https://miaotixing.com/trigger";


    /**
     * 触发喵提醒
     */
    public static MiaoTriggerResponse trigger(String id) {
        MiaoTriggerRequest request = new MiaoTriggerRequest();
        request.setId(id);
        request.setType("json");
        return trigger(request);
    }

    public static MiaoTriggerResponse trigger(MiaoTriggerRequest request) {
        Map<String, Object> paramsMap = BeanUtil.beanToMap(request, false, true);
        String respStr = HttpUtil.post(TRIGGER_URL, paramsMap);
        MiaoTriggerResponse response = JSONUtil.parse(respStr, MiaoTriggerResponse.class);
        return response;
    }
}
