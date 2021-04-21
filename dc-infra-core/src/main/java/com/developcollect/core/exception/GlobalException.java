package com.developcollect.core.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 全局受检异常
 * 抛出该异常可以在调用时必须检查(try catch)，否则编译无法通过
 *
 * @author zak
 * @version 1.0
 * @date 2020/10/16 16:25
 */
public class GlobalException extends Exception implements IException {

    /**
     * 异常状态码，也是返回值的状态码
     */
    @Getter
    protected int code = DEFAULT_CODE;


    /**
     * 无参构造函数
     */
    public GlobalException() {
    }

    /**
     * 指定异常状态码和异常消息
     *
     * @param code 异常状态码
     * @param msg  异常消息
     */
    public GlobalException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public GlobalException(int code) {
        this(code, "内部错误");
    }

    /**
     * 指定异常消息创建异常
     * 异常编码使用默认的500
     *
     * @param msg 异常消息
     */
    public GlobalException(String msg) {
        super(msg);
    }


    /**
     * 指定异常状态码，异常消息和包装异常
     *
     * @param code      异常状态码
     * @param msg       异常消息
     * @param throwable 包装异常
     */
    public GlobalException(int code, String msg, Throwable throwable) {
        super(msg, throwable);
        this.code = code;
    }

    /**
     * 指定异常消息和包装异常
     *
     * @param msg       异常消息
     * @param throwable 包装异常
     */
    public GlobalException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    /**
     * 指定异常状态码和包装异常
     *
     * @param code      异常状态码
     * @param throwable 包装异常
     */
    public GlobalException(int code, Throwable throwable) {
        super(throwable);
        this.code = code;
    }


    /**
     * 指定异常状态码、异常消息和包装异常
     * 其中异常消息支持参数格式化，格式化规则同log消息规则
     * 以字符串"{}"为占位符
     * 例如：
     * long id = 123L;
     * new GlobalException(400, "id[{}]不存在", id)
     * 那么创建的异常的消息就是 "id[123]不存在"
     * <p>
     * 如果需要包装异常，那么只需要把包装异常参数放到格式化参数后面，也就是整串参数的最后一个
     * 例如：
     * try {
     * long id = 123L;
     * ...
     * } catch (Exception e) {
     * throw new GlobalException(400, "id[{}]不存在", id, e)
     * }
     *
     * @param code   异常代码
     * @param format 异常消息格式化字符串
     * @param params 异常消息格式化参数
     */
    public GlobalException(int code, String format, Object... params) {
        super(StrUtil.format(format, params), IException.mayThrowable(params));
        this.code = code;

    }

    /**
     * 指定异常消息，异常消息可以是格式化串， 最后一个参数可以是包装异常
     * <p>
     * 具体使用{@link #GlobalException(int, String, Object...)}
     *
     * @param format 格式化串
     * @param params 格式化参数和包装异常
     */
    public GlobalException(String format, Object... params) {
        super(StrUtil.format(format, params), IException.mayThrowable(params));
    }


    /**
     * 指定包装异常，使用默认异常状态码，使用包装异常的消息创建一个异常
     *
     * @param throwable 被包装异常
     */
    public GlobalException(Throwable throwable) {
        super(throwable);
    }


}
