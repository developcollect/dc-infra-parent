package com.developcollect.core.utils;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class CglibUtil {


    /**
     * 动态添加类的属性
     * 通过cglib创建目标类的子类, 并增加指定的字段
     * 然后会把原对象的属性值和待增加的属性的属性值复制到新的对象中
     *
     * @param dest          待增强的类对象
     * @param addProperties 待增加的属性及属性值
     * @return 增强后的对象
     * @author zak
     * @date 2019
     */
    public static Object dynamicField(Object dest, Map<String, ?> addProperties) {
        // 动态创建对象, 增加指定属性
        Map<String, Class> propertyMap = new HashMap<>(addProperties.size());
        for (String key : addProperties.keySet()) {
            propertyMap.put(key, addProperties.get(key).getClass());
        }
        DynamicBean dynamicBean = new DynamicBean(dest.getClass(), propertyMap);

        // 把扩展属性值复制到新对象
        addProperties.forEach((k, v) -> dynamicBean.setValue(k, v));
        Object target = dynamicBean.getTarget();
        // 把原对象中的属性值复制到新对象
        BeanUtil.copyProperties(dest, target);

        return target;
    }


    /**
     * 生成动态代理, 并将原有的成员属性复制到代理对象
     *
     * @param target
     * @param methodInterceptor
     * @return T 类型
     * @author zak
     * @date 2020/8/26 10:22
     */
    public static <T> T proxy(T target, MethodInterceptor methodInterceptor) {
        if (target instanceof Class) {
            return proxy((Class<T>) target, methodInterceptor);
        }
        T proxy = (T) proxy(target.getClass(), methodInterceptor);
        // 复制原有属性到代理对象
        BeanUtil.copyProperties(target, proxy);
        return proxy;
    }


    /**
     * 生成动态代理
     *
     * @param clazz
     * @param methodInterceptor
     * @return T 类型
     * @author zak
     * @date 2020/8/26 10:22
     */
    public static <T> T proxy(Class<T> clazz, MethodInterceptor methodInterceptor) {
        // 创建Enhancer对象
        Enhancer enhancer = new Enhancer();
        // 设置目标类的字节码文件
        enhancer.setSuperclass(clazz);
        // 设置回调函数
        enhancer.setCallback(methodInterceptor);

        // 这里的creat方法就是正式创建代理类对象
        T proxy = (T) enhancer.create();
        return proxy;
    }


    private static class DynamicBean {
        /**
         * 目标对象
         */
        private Object target;

        /**
         * 属性集合
         */
        private BeanMap beanMap;

        DynamicBean(Class superclass, Map<String, Class> propertyMap) {
            this.target = generateBean(superclass, propertyMap);
            this.beanMap = BeanMap.create(this.target);
        }


        /**
         * bean 添加属性和值
         *
         * @param property
         * @param value
         */
        void setValue(String property, Object value) {
            log.debug("beanMap.put => {}:{}", property, value);
            beanMap.put(property, value);
        }

        /**
         * 获取属性值
         *
         * @param property
         * @return
         */
        Object getValue(String property) {
            return beanMap.get(property);
        }

        /**
         * 获取对象
         *
         * @return
         */
        Object getTarget() {
            return this.target;
        }


        /**
         * 根据属性生成对象
         *
         * @param superclass
         * @param propertyMap
         * @return
         */
        private Object generateBean(Class superclass, Map<String, Class> propertyMap) {
            BeanGenerator generator = new BeanGenerator();
            if (null != superclass) {
                generator.setSuperclass(superclass);
            }
            BeanGenerator.addProperties(generator, propertyMap);
            return generator.create();
        }
    }
}
