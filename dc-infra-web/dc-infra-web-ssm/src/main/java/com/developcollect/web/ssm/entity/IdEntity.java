package com.developcollect.web.ssm.entity;


import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;


@Data
@KeySequence(value = "SEQ_MYSQL_LONG_KEY")
public abstract class IdEntity<ID extends Serializable> implements IIdEntity<ID> {

    @TableId
    protected ID id;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IdEntity<?> idEntity = (IdEntity<?>) o;
        return Objects.equals(getId(), idEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

