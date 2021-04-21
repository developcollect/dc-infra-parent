package com.developcollect.core.lang.annotation;

import java.io.Serializable;
import java.util.function.LongPredicate;


@FunctionalInterface
public interface SerializableLongPredicate extends Serializable, LongPredicate {


}
