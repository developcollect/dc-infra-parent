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
public class DcException extends Exception implements IExceptionInfo {

    @Getter
    private String code;

    public DcException(IExceptionInfo exceptionInfo) {
        super(exceptionInfo.getMessage());
        this.code = exceptionInfo.getCode();
    }

    public DcException(IExceptionInfo exceptionInfo, Throwable throwable) {
        super(exceptionInfo.getMessage(), throwable);
        this.code = exceptionInfo.getCode();
    }

    public DcException(IExceptionInfo exceptionInfo, String format, Object... params) {
        super(StrUtil.format(format, params), IExceptionInfo.mayThrowable(params));
        this.code = exceptionInfo.getCode();
    }
}
