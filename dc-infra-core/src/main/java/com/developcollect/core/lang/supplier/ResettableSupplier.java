package com.developcollect.core.lang.supplier;

import com.developcollect.core.lang.Resettable;

import java.util.function.Supplier;

public interface ResettableSupplier<T> extends Supplier<T>, Resettable {
}
