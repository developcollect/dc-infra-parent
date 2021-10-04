package com.developcollect.web.ssm.entity;

import com.developcollect.core.tree.IdTree;

import java.io.Serializable;

public interface ITreeEntity<T extends ITreeEntity<T, ID>, ID extends Serializable> extends IIdEntity<ID>, IdTree<T, ID> {


}
