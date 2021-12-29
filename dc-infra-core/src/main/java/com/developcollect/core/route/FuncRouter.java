package com.developcollect.core.route;


import lombok.Getter;

import java.util.List;
import java.util.function.Function;

public class FuncRouter<T> implements Router<T> {

    @Getter
    protected List<T> elements;
    @Getter
    protected Function<List<T>, T> routeFunc;

    public FuncRouter(List<T> elements, Function<List<T>, T> routeFunc) {
        this.elements = elements;
        this.routeFunc = routeFunc;
    }

    @Override
    public T next() {
        return routeFunc.apply(elements);
    }


}
