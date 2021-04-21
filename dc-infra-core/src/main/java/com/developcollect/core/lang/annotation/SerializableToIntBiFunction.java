package com.developcollect.core.lang.annotation;

import java.io.Serializable;
import java.util.function.ToIntBiFunction;


@FunctionalInterface
public interface SerializableToIntBiFunction<T, U> extends Serializable, ToIntBiFunction<T, U> {


}
