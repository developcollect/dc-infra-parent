package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.LongToIntFunction;


@FunctionalInterface
public interface SerializableLongToIntFunction extends Serializable, LongToIntFunction {

}
