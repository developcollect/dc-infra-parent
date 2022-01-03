package com.developcollect.core.lang.retry;

public class RuntimeTimeoutException extends RuntimeException {

    public RuntimeTimeoutException() {
        super();
    }

    public RuntimeTimeoutException(String message) {
        super(message);
    }

    public RuntimeTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeTimeoutException(Throwable cause) {
        super(cause);
    }

    protected RuntimeTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
