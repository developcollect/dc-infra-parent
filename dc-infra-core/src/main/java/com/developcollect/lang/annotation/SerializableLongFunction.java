package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.LongFunction;

@FunctionalInterface
public interface SerializableLongFunction<R> extends Serializable, LongFunction<R> {


}
