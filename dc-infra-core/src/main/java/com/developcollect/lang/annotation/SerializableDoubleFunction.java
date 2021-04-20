package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface SerializableDoubleFunction<R> extends Serializable, DoubleFunction<R> {

}
