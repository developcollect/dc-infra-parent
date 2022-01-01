package com.developcollect.core.lang.route;

import java.util.List;
import java.util.function.ToIntFunction;

/**
 * 通过将路由参数映射为索引下标实现的路由器
 *
 * @param <P> 路由参数类型
 * @param <O> 路由结果类型
 * @author zak
 */
public class IndexRouter<P, O> implements Router<P, O> {
    private final FuncRouter<P, O> fr;

    public IndexRouter(List<O> elements, ToIntFunction<P> indexFunc) {
        this.fr = new FuncRouter<>(elements, (eles, p) -> eles.get(indexFunc.applyAsInt(p)));
    }

    @Override
    public O route(P p) {
        return fr.route(p);
    }
}
