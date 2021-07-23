package com.developcollect.core.utils.miaotixing;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MiaoTriggerResponse {

    /**
     * 执行结果，0为成功或部分成功，非0时为失败，详见《错误代码解释》。
     */
    private int code;

    /**
     * 原计划要推送的人数。
     */
    private int users;

    /**
     * 实际推送情况，包括：
     */
    @JSONField(name = "success_sent")
    @JsonProperty("success_sent")
    private List<SendResult> successSent;


    /**
     * 	推送结果文字描述。当code不为0时，该参数为错误信息描述。
     */
    private String msg;


    @Data
    public static class SendResult {

        /**
         * 公众号免费提醒成功推送人数。
         */
        private int mptext;

        /**
         * 短信提醒成功推送人数。
         */
        private int sms;

        /**
         * 语音电话提醒成功推送人数。
         */
        private int phonecall;
    }
}
