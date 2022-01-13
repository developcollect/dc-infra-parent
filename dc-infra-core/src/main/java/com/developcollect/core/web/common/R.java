package com.developcollect.core.web.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 提供返回值的封装
 *
 * @param <T>
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    /**
     * 通用成功代码
     */
    public static final String COMMON_SUCCESS_CODE = "200";

    /**
     * 通用客户端错误代码
     */
    public static final String COMMON_CLIENT_FAIL_CODE = "400";

    /**
     * 通用服务端错误代码
     */
    public static final String COMMON_SERVER_FAIL_CODE = "500";

    /**
     * 状态代码
     * <p>
     * 成功： 200
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;



    /**
     * 以消息和数据构建一个成功的返回结果包装
     * 状态码固定为200
     *
     * @param message 消息
     * @param data    承载数据
     * @return JcResult<E>
     * @author Zhu Kaixiao
     * @date 2020/9/26 11:17
     */
    public static <E> R<E> ok(String message, E data) {
        return of(COMMON_SUCCESS_CODE, message, data);
    }


    /**
     * 以数据构建一个成功的返回结果包装
     * 状态码固定为200，消息固定为“成功”
     *
     * @param data 承载数据
     * @return JcResult<E>
     * @author Zhu Kaixiao
     * @date 2020/9/26 11:17
     */
    public static <E> R<E> ok(E data) {
        return of(COMMON_SUCCESS_CODE, "成功", data);
    }


    /**
     * 构建一个默认的成功的返回结果包装
     * 状态码固定为200， 消息固定为“成功”
     *
     * @return JcResult<E>
     * @author Zhu Kaixiao
     * @date 2020/9/26 11:17
     */
    public static <E> R<E> ok() {
        return of(COMMON_SUCCESS_CODE, "成功");
    }


    public static <E> R<E> fail(String message, E data) {
        return of(COMMON_SERVER_FAIL_CODE, message, data);
    }

    public static <E> R<E> fail(E data) {
        return of(COMMON_SERVER_FAIL_CODE, "内部错误", data);
    }

    public static <E> R<E> fail() {
        return of(COMMON_SERVER_FAIL_CODE, "内部错误");
    }




    public static <E> R<E> of(int code, String message) {
        return of(String.valueOf(code), message);
    }

    public static <E> R<E> of(int code, String message, E data) {
        return of(String.valueOf(code), message, data);
    }

    public static <E> R<E> of(String code, String message) {
        return new R<>(code, message, null);
    }

    public static <E> R<E> of(String code, String message, E data) {
        return new R<>(code, message, data);
    }

    public static <T> R<T> create() {
        return new R<>();
    }

    /**
     * 判断返回结果是否成功
     * 也就是判断状态码是否200
     *
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/9/26 11:22
     */
    public boolean success() {
        return COMMON_SUCCESS_CODE.equals(this.getCode());
    }

    public boolean failed() {
        return !success();
    }

}
