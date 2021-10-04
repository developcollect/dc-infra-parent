package com.developcollect.core.lang.holder;

import java.io.Serializable;

public interface IdHolder<ID extends Serializable> {

    ID getId();

}
