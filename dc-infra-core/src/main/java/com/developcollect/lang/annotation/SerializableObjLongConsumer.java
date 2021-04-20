package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.ObjLongConsumer;

@FunctionalInterface
public interface SerializableObjLongConsumer<T> extends Serializable, ObjLongConsumer<T> {

}
