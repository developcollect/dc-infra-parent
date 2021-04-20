
package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.BiPredicate;


@FunctionalInterface
public interface SerializableBiPredicate<T, U> extends Serializable, BiPredicate<T, U> {

}
