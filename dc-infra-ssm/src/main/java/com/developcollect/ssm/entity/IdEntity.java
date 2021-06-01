package com.developcollect.ssm.entity;


import com.baomidou.mybatisplus.annotation.KeySequence;
import lombok.Data;

import java.io.Serializable;


@Data
@KeySequence(value = "SEQ_MYSQL_LONG_KEY")
public abstract class IdEntity<ID extends Serializable> {
    private ID id;
}

