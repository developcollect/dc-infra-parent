package com.developcollect.web.common.http;

public interface MutableRequest {


    void setBody(byte[] bytes);


    default void setBody(String body) {
        setBody(body.getBytes());
    }

}
