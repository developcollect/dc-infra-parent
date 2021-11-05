package com.developcollect.core.exception;


import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 全局运行时异常
 * 抛出该异常可以在调用时不用检查(try catch)，直接通过全局异常处理
 * 如果有需要也可以在调用时检查
 *
 * @author zak
 * @version 1.0
 * @date 2019/11/1 13:51
 */
public class DcRuntimeException extends RuntimeException implements IExceptionInfo {


    @Getter
    private String code;

    public DcRuntimeException(IExceptionInfo exceptionInfo) {
        super(exceptionInfo.getMessage());
        this.code = exceptionInfo.getCode();
    }

    public DcRuntimeException(IExceptionInfo exceptionInfo, Throwable throwable) {
        super(exceptionInfo.getMessage(), throwable);
        this.code = exceptionInfo.getCode();
    }

    /**
     * 指定异常状态码、异常消息和包装异常
     * 其中异常消息支持参数格式化，格式化规则同log消息规则
     * 以字符串"{}"为占位符
     * 例如：
     * long id = 123L;
     * new GlobalRuntimeException(400, "id[{}]不存在", id)
     * 那么创建的异常的消息就是 "id[123]不存在"
     * <p>
     * 如果需要包装异常，那么只需要把包装异常参数放到格式化参数后面，也就是整串参数的最后一个
     * 例如：
     * try {
     * long id = 123L;
     * ...
     * } catch (Exception e) {
     * throw new GlobalRuntimeException(400, "id[{}]不存在", id, e)
     * }
     *
     * @param exceptionInfo 异常信息
     * @param format        异常消息格式化字符串
     * @param params        异常消息格式化参数
     */
    public DcRuntimeException(IExceptionInfo exceptionInfo, String format, Object... params) {
        super(StrUtil.format(format, params), IExceptionInfo.mayThrowable(params));
        this.code = exceptionInfo.getCode();
    }

}
