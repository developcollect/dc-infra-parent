package com.developcollect.cache;

/**
 * 值反序列化接口
 */
public interface ValueDeserializer<S, T> {

    T deserialize(S s);
}
