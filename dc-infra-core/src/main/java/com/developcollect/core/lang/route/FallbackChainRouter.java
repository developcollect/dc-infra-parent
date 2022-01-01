package com.developcollect.core.lang.route;

import com.developcollect.core.utils.CollUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 包含备用结果的路由器
 * 路由出一个结果后，其他剩余的结果都作为后备结果
 *
 * @param <P>
 * @param <O>
 */
public class FallbackChainRouter<P, O> implements Router<P, List<O>> {

    protected List<O> elements;
    protected Router<P, O> delegate;

    /**
     * 创建包含备用结果的路由器，指定的可用元素应与主结果路由器中的可用元素一致
     *
     * @param elements 可用元素
     * @param router   主结果路由器
     */
    public FallbackChainRouter(List<O> elements, Router<P, O> router) {
        this.elements = CollUtil.distinct(new ArrayList<>(elements));
        this.delegate = router;
    }

    /**
     * 路由出一个结果后，其他剩余的结果都作为后备结果
     *
     * @param p 路由输入参数
     * @return 路由的结果，集合中第一个元素是路由方法得出的值，之后的值都是备用结果
     */
    @Override
    public List<O> route(P p) {
        O head = routeHead(p);
        LinkedList<O> list = new LinkedList<>(elements);
        list.remove(head);
        list.addFirst(head);
        return list;
    }


    /**
     * 得出主结果，也就是返回集合的头元素
     */
    private O routeHead(P p) {
        return delegate.route(p);
    }

}
