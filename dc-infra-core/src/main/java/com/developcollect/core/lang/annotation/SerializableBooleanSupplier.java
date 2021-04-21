
package com.developcollect.core.lang.annotation;


import java.io.Serializable;
import java.util.function.BooleanSupplier;


@FunctionalInterface
public interface SerializableBooleanSupplier extends Serializable, BooleanSupplier {

}
