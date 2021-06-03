package com.developcollect.web.ssm.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.developcollect.core.utils.LambdaUtil;

import java.io.Serializable;
import java.util.Objects;

public class ServiceImpl<M extends BaseMapper<T>, T> extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<M, T> implements IService<T> {

    @Override
    protected Class<T> currentMapperClass() {
        return (Class<T>) this.getResolvableType().as(ServiceImpl.class).getGeneric(0).getType();
    }

    @Override
    protected Class<T> currentModelClass() {
        return (Class<T>) this.getResolvableType().as(ServiceImpl.class).getGeneric(1).getType();
    }

    @Override
    public <V> T getByField(SFunction<T, V> fieldGetter, V val) {
        return getOne(Wrappers.<T>query().eq(LambdaUtil.getFieldName(fieldGetter), val));
    }

    @Override
    public boolean exist(Serializable id) {
        return exist(Wrappers.<T>query().eq("id", id));
    }

    @Override
    public <V> boolean exist(SFunction<T, V> function, V val) {
        return exist(Wrappers.<T>query().eq(LambdaUtil.getFieldName(function), val));
    }

    @Override
    public boolean exist(Wrapper<T> queryWrapper) {
        return getBaseMapper().selectCount(queryWrapper) > 0;
    }

    @Override
    public <V> boolean checkAttrOwner(Serializable id, SFunction<T, V> fieldGetter, V val) {
        T entity = getById(id);
        return entity != null && Objects.equals(fieldGetter.apply(entity), val);
    }

    @Override
    public <V> boolean checkAttrAvailable(Serializable id, SFunction<T, V> fieldGetter, V val) {
        T entity = getByField(fieldGetter, val);
        return entity == null || Objects.equals(getId(entity), id);
    }


    protected Serializable getId(T entity) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        Serializable idVal = (Serializable) ReflectionKit.getFieldValue(entity, tableInfo.getKeyProperty());
        return idVal;
    }
}
