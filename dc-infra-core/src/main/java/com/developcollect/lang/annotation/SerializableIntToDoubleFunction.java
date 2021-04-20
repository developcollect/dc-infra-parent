package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.IntToDoubleFunction;


@FunctionalInterface
public interface SerializableIntToDoubleFunction extends Serializable, IntToDoubleFunction {


}
