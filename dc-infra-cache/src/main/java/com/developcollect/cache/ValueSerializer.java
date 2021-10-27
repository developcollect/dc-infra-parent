package com.developcollect.cache;

/**
 * 值序列化接口
 */
public interface ValueSerializer<T, S> {

    S serialize(T t);

}
