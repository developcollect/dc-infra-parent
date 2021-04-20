package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.ToLongFunction;


@FunctionalInterface
public interface SerializableToLongFunction<T> extends Serializable, ToLongFunction<T> {


}
