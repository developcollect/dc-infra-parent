package com.developcollect.web.ssm.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.developcollect.core.utils.CollUtil;

import java.util.function.Function;

public class PageUtil {

    /**
     * 快速转换一个page对象
     * @param page 原page对象
     * @param convertor 转换器
     */
    public static <T, R> IPage<R> convert(IPage<T> page, Function<T, R> convertor) {
        Page<R> newPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        newPage.setRecords(CollUtil.toList(page.getRecords(), convertor));
        return newPage;
    }


    public static <T> IPage<T> defaultOrder(IPage<T> page, OrderItem... orderItems) {
        if (CollUtil.isNotEmpty(page.orders())) {
            return page;
        }
        if (page instanceof Page) {
            Page<T> p1 = (Page<T>) page;
            p1.addOrder(orderItems);
            return p1;
        } else {
            Page<T> p2 = new Page<>(page.getCurrent(), page.getSize());
            p2.addOrder(orderItems);
            return p2;
        }
    }
}
