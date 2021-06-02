package com.developcollect.web.ssm.entity;

import lombok.ToString;

import java.io.Serializable;


@ToString(callSuper = true)
public abstract class PhysicalDeletionEntity<ID extends Serializable> extends CommonEntity<ID> {
}
