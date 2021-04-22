package com.developcollect.web;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/22 13:18
 */
public class HttpResult<T> {

    /**
     * 通用成功代码
     */
    public static final int COMMON_SUCCESS_CODE = 200;

    /**
     * 通用客户端错误代码
     */
    public static final int COMMON_CLIENT_FAIL_CODE = 400;

    /**
     * 通用服务端错误代码
     */
    public static final int COMMON_SERVER_FAIL_CODE = 500;

    /**
     * 状态代码
     * <p>
     * 成功： 200
     * 错误： 见异常代码值 {@link IExceptionInfo}
     */
    private int code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public HttpResult() {
    }

    public HttpResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    /**
     * 以状态码和消息构建一个成功的返回结果包装
     *
     * @param code    状态码
     * @param message 消息
     * @return JcResult<E>
     * @author Zhu Kaixiao
     * @date 2020/9/26 11:17
     */
    public static <E> HttpResult<E> ok(int code, String message) {
        return build(code, message);
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
    public static <E> HttpResult<E> ok(String message, E data) {
        return build(COMMON_SUCCESS_CODE, message, data);
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
    public static <E> HttpResult<E> ok(E data) {
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
    public static <E> HttpResult<E> ok() {
        return ok(null);
    }


    public static <E> HttpResult<E> fail(int code, String message) {
        return build(code, message);
    }

    /***
     * 系统异常消息，不要主动抛出，需要国际化
     * @param message 系统错误消息
     * @param <E>
     * @return
     */
    public static <E> HttpResult<E> failMsg(String message) {
        return new HttpResult<>(COMMON_SERVER_FAIL_CODE, message, null);
    }

    public static <E> HttpResult<E> fail(E data) {
        return build(COMMON_SERVER_FAIL_CODE, "", data);
    }

    public static <E> HttpResult<E> fail() {
        return fail(null);
    }


    public static <E> HttpResult<E> build(int code, String message) {
        return new HttpResult<>(code, message, null);
    }

    public static <E> HttpResult<E> build(int code, String message, E data) {
        return new HttpResult<>(code, message, data);
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
        return getCode() >= COMMON_SUCCESS_CODE && getCode() < 300;
    }


    public boolean failed() {
        return !success();
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public HttpResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public HttpResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public HttpResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "JcResult(code=" + this.getCode() + ", message=" + this.getMessage() + ", data=" + this.getData() + ")";
    }


}

