package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface SerializableIntConsumer extends Serializable, IntConsumer {


}
