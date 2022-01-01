package com.developcollect.core.lang.route;


import lombok.Getter;

import java.util.List;
import java.util.function.BiFunction;

public class FuncRouter<P, O> implements Router<P, O> {

    @Getter
    protected List<O> elements;
    @Getter
    protected BiFunction<List<O>, P, O> routeFunc;

    public FuncRouter(List<O> elements, BiFunction<List<O>, P, O> routeFunc) {
        this.elements = elements;
        this.routeFunc = routeFunc;
    }


    @Override
    public O route(P p) {
        return routeFunc.apply(elements, p);
    }
}
