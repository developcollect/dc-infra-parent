package com.developcollect.exception;

import java.io.Serializable;

/**
 * @author zak
 * @version 1.0
 * @date 2020/10/16 16:26
 */
public interface IException extends Serializable {

    int DEFAULT_CODE = 500;

    int getCode();

    String getMessage();

    static Throwable mayThrowable(Object... params) {
        if (params != null && params.length > 0 && params[params.length - 1] instanceof Throwable) {
            return (Throwable) params[params.length - 1];
        }
        return null;
    }
}
