package com.developcollect.core.route;


import com.developcollect.core.utils.CollUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HashRouter<P, O> implements Router<P, O> {
    protected Map<String, O> elementMap;
    protected Function<P, String> parameterHash;

    public HashRouter(List<O> elements, Function<O, String> elementHash, Function<P, String> parameterHash) {
        this(CollUtil.toMap(elements, elementHash, e -> e), parameterHash);
    }

    public HashRouter(Map<String, O> elementMap, Function<P, String> parameterHash) {
        this.elementMap = elementMap;
        this.parameterHash = parameterHash;
    }

    @Override
    public O route(P p) {
        return elementMap.get(parameterHash.apply(p));
    }
}
