package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.DoubleToLongFunction;


@FunctionalInterface
public interface SerializableDoubleToLongFunction extends Serializable, DoubleToLongFunction {


}
