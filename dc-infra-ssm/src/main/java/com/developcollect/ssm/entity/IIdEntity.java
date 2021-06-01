package com.developcollect.ssm.entity;

import java.io.Serializable;

public interface IIdEntity<ID extends Serializable> {

    ID getId();

}
