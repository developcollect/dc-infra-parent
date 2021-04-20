package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author zak
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Serializable, Function<T, R> {
}
