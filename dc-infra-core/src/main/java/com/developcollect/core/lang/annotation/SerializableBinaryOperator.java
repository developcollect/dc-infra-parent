
package com.developcollect.core.lang.annotation;

import java.io.Serializable;
import java.util.function.BinaryOperator;


@FunctionalInterface
public interface SerializableBinaryOperator<T> extends Serializable, BinaryOperator<T> {

}
