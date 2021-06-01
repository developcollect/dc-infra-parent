package com.developcollect.core.tree;

import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

public class TreeUtilTest {


    @Test
    public void test2() {
        List<TestTreeNode> nodes = new ArrayList<>();

        TestVO vo  = TreeUtil.convertToTreeAndSort(nodes, tnode -> null, null);


    }



}