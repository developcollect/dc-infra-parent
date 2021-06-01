package com.developcollect.ssm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 逻辑删除基类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class LogicallyDeletionEntity<ID extends Serializable> extends CommonEntity<ID> {

    /**
     * 是否已逻辑删除
     * 1 已删除
     * 0 未删除
     */
    private int deleteFlag;

    public boolean deleted() {
        return deleteFlag == 1;
    }

    public boolean undeleted() {
        return deleteFlag == 0;
    }
}
