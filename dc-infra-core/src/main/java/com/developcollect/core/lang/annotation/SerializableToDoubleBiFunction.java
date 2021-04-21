package com.developcollect.core.lang.annotation;

import java.io.Serializable;
import java.util.function.ToDoubleBiFunction;

@FunctionalInterface
public interface SerializableToDoubleBiFunction<T, U> extends Serializable, ToDoubleBiFunction<T, U> {

}
