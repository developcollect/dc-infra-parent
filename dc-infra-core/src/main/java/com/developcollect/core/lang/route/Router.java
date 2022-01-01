package com.developcollect.core.lang.route;


/**
 * 路由器接口
 *
 * @param <P> 路由输入参数
 * @param <O> 路由输出结果
 * @author zak
 */
public interface Router<P, O> {

    /**
     * 根据输入参数路由一个结果
     *
     * @param p 路由输入参数
     * @return 路由输出结果
     */
    O route(P p);

}
