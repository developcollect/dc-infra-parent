package com.developcollect.web.ssm.entity;


import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;


@Data
@KeySequence(value = "SEQ_MYSQL_LONG_KEY")
public abstract class IdEntity<ID extends Serializable> implements IIdEntity<ID> {

    @TableId
    protected ID id;
}

