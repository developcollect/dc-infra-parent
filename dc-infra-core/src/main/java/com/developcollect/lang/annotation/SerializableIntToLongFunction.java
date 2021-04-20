package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.IntToLongFunction;


@FunctionalInterface
public interface SerializableIntToLongFunction extends Serializable, IntToLongFunction {


}
