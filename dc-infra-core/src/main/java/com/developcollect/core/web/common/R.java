package com.developcollect.core.web.common;


/**
 * 提供返回值的封装
 *
 * @param <T>
 */
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

    public R() {
    }

    public R(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


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
        return build(COMMON_SUCCESS_CODE, message, data);
    }

    public static <E> R<E> okMsg(String message) {
        return build(COMMON_SUCCESS_CODE, message, null);
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
        return ok("成功", data);
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
        return ok(null);
    }


    /***
     * 系统异常消息，不要主动抛出，需要国际化
     * @param message 系统错误消息
     * @param <E>
     * @return
     */
    public static <E> R<E> failMsg(String message) {
        return new R<>(COMMON_SERVER_FAIL_CODE, message, null);
    }

    public static <E> R<E> fail(E data) {
        return build(COMMON_SERVER_FAIL_CODE, "", data);
    }

    public static <E> R<E> fail() {
        return fail(null);
    }



    public static <E> R<E> build(int code, String message) {
        return build(String.valueOf(code), message);
    }

    public static <E> R<E> build(int code, String message, E data) {
        return build(String.valueOf(code), message, data);
    }

    public static <E> R<E> build(String code, String message) {
        return new R<>(code, message, null);
    }

    public static <E> R<E> build(String code, String message, E data) {
        return new R<>(code, message, data);
    }


    /**
     * 判断返回结果是否成功
     * 也就是判断状态码是否在 200(含)-300(不含)之间
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

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public R<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public R<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public R<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "R(code=" + this.getCode() + ", message=" + this.getMessage() + ", data=" + this.getData() + ")";
    }

}
