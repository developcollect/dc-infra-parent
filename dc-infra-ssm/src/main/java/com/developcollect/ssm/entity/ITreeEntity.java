package com.developcollect.ssm.entity;

import com.developcollect.core.tree.IIdTree;

import java.io.Serializable;

public interface ITreeEntity<T extends ITreeEntity<T, ID>, ID extends Serializable> extends IIdEntity<ID>, IIdTree<T, ID> {


}
