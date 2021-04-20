package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.IntUnaryOperator;


@FunctionalInterface
public interface SerializableIntUnaryOperator extends Serializable, IntUnaryOperator {

}
