package com.developcollect.web.common.http;

/**
 * 容器
 */
public interface BodyContainer {


    void setBody(byte[] bytes);


    default void setBody(String body) {
        setBody(body.getBytes());
    }


    byte[] getBody();

}
