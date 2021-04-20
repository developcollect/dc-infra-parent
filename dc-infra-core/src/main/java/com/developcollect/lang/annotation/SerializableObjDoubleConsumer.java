package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.ObjDoubleConsumer;


@FunctionalInterface
public interface SerializableObjDoubleConsumer<T> extends Serializable, ObjDoubleConsumer<T> {


}
