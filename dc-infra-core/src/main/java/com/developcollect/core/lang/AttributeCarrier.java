package com.developcollect.core.lang;

/**
 * 属性载体载体
 */
public interface AttributeCarrier {


    /**
     * 设置属性
     *
     * @param name 属性名称
     * @param o    属性值
     */
    void setAttribute(String name, Object o);

    /**
     * 获取属性，属性不存在则返回null
     *
     * @param name 属性名称
     * @param <T>  属性类型
     * @return 属性值
     */
    <T> T getAttribute(String name);

    /**
     * 删除一个属性
     *
     * @param name 属性名称
     */
    <T> T removeAttribute(String name);

}
