package com.developcollect.web.ssm.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 逻辑删除基类
 */
@Data
public abstract class LogicallyDeletionEntity<ID extends Serializable> extends CommonEntity<ID> {

    /**
     * 是否已逻辑删除
     * 1 已删除
     * 0 未删除
     */
    @TableLogic
    private int deleteFlag;

    public boolean deleted() {
        return deleteFlag == 1;
    }

    public boolean undeleted() {
        return deleteFlag == 0;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
