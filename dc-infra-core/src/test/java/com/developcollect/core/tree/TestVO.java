package com.developcollect.core.tree;

import com.developcollect.core.lang.holder.AbstractValueHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestVO extends AbstractValueHolder<String> implements IMasterNode<TestVO>  {

    private List<TestVO> children;



}
