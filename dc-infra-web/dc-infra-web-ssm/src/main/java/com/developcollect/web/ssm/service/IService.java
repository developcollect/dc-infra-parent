package com.developcollect.web.ssm.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.io.Serializable;

public interface IService<T> extends com.baomidou.mybatisplus.extension.service.IService<T> {

    <V> T getByField(SFunction<T, V> fieldGetter, V val);

    boolean exist(Serializable id);

    <V> boolean exist(SFunction<T, V> function, V val);

    boolean exist(Wrapper<T> queryWrapper);

    /**
     * 检查指定属性值是否指定的id所有
     *
     * @param id          id
     * @param fieldGetter
     * @param val
     * @param <V>
     */
    <V> boolean checkAttrOwner(Serializable id, SFunction<T, V> fieldGetter, V val);

    /**
     * 检查指定属性值在指定的id下是否可用
     * 可用有两种情况：
     * 1. 属性值本来就属于该id
     * 2. 属性值无主
     * <p>
     * 例子:
     * 判断用户名“张三”在id 123 下是否可用
     * 如果id 123的用户名本来就是“张三”， 那么就可用的
     * 如果用户名“张三”没用任何一个用户使用，那么也是可用的
     *
     * @param id          id
     * @param fieldGetter 属性值方法
     * @param val         属性值
     * @param <V>         属性值泛型
     * @return 是否可用
     */
    <V> boolean checkAttrAvailable(Serializable id, SFunction<T, V> fieldGetter, V val);
}
