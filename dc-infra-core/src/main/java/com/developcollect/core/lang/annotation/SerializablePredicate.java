package com.developcollect.core.lang.annotation;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * @author zak
 */
@FunctionalInterface
public interface SerializablePredicate<T> extends Serializable, Predicate<T> {
}
