package com.developcollect.ssm.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public class ServiceImpl<M extends BaseMapper<T>, T> extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<M, T> {

    @Override
    protected Class<T> currentMapperClass() {
        return (Class<T>) this.getResolvableType().as(ServiceImpl.class).getGeneric(0).getType();
    }

    @Override
    protected Class<T> currentModelClass() {
        return (Class<T>) this.getResolvableType().as(ServiceImpl.class).getGeneric(1).getType();
    }
}
