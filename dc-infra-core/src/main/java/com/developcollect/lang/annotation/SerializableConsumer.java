package com.developcollect.lang.annotation;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author zak
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends Serializable, Consumer<T> {
}
