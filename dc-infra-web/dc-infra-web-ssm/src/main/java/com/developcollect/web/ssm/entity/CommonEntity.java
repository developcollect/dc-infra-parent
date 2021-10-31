package com.developcollect.web.ssm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public abstract class CommonEntity<ID extends Serializable> extends IdEntity<ID> {

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    protected Long createUser;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    protected LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    protected Long updateUser;

    @TableField(fill = FieldFill.UPDATE)
    protected LocalDateTime updateTime;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
