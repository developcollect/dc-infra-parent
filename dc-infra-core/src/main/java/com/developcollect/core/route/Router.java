package com.developcollect.core.route;

import com.developcollect.core.lang.Resettable;

public interface Router<T> extends Resettable {

    T next();


    @Override
    default void reset() {
        // do nothing
    }
}
