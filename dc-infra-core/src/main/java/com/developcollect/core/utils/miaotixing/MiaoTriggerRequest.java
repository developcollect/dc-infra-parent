package com.developcollect.core.utils.miaotixing;


import lombok.Data;

@Data
public class MiaoTriggerRequest {

    /**
     * 喵码。指定发出的提醒，一个提醒对应一个喵码。（必填）
     */
    private String id;
    /**
     * 提醒附加内容。收到的提醒会换行后显示该内容，默认为空。
     */
    private String text;

    /**
     * 值为json或jsonp。为json时返回json格式数据；为jsonp时返回jsonp格式数据；默认为空，即返回文字描述信息。	json
     */
    private String type = "json";

    /**
     * 当type为jsonp时，callback可指定回调函数名；默认为miaotixing_jsonpcallback。	mycallback
     */
    private String callback;

    /**
     * 请求时的10位数时间戳，单位：秒。传入该参数时，喵提醒将检查当前时间与时间戳相差值，如果相差超过60秒则拒绝请求。不传该参数时不做时间检查。	1615865402
     */
    private Integer ts;

    /**
     * 套用提醒模板参数。传入该值时，喵提醒将检查提醒模板与参数格式是否匹配，匹配成功后将用提醒模板取代公众号文字提醒方式，详见《提醒模板使用方法》。不传该参数时不使用提醒模板。
     */
    private String templ;

    /**
     * 开发者产品id。传入该值时，系统将标记本次提醒关联的开发者，并享受开发者服务。默认为无关联的开发者。
     */
    private String product;

    /**
     * 附加选项参数，多个参数之间用逗号隔开。支持以下参数：
     *     nosms（暂屏蔽短信通知）
     *     nophonecall（暂屏蔽语音电话通知）
     *     nosms,nophonecall
     */
    private String option;
}
