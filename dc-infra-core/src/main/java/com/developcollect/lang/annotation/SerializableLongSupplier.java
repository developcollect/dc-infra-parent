package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.LongSupplier;

@FunctionalInterface
public interface SerializableLongSupplier extends Serializable, LongSupplier {


}
