package com.developcollect.core.exception;

import java.io.Serializable;

/**
 * @author zak
 * @version 1.0
 * @date 2020/10/16 16:26
 */
public interface IExceptionInfo extends Serializable {

    String DEFAULT_CODE = "500";

    String getCode();

    String getMessage();

    static Throwable mayThrowable(Object... params) {
        if (params != null && params.length > 0 && params[params.length - 1] instanceof Throwable) {
            return (Throwable) params[params.length - 1];
        }
        return null;
    }
}
