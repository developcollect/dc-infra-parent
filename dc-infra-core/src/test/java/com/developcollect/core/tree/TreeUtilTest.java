package com.developcollect.core.tree;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.*;

public class TreeUtilTest {


    @Test
    public void test2() {
        List<TestTreeNode> nodes = new ArrayList<>();
        TestVO vo  = TreeUtil.convertToTreeAndSort(nodes, tnode -> null, null);
    }


    /**
     * <img src="https://img2018.cnblogs.com/blog/1031555/201905/1031555-20190505124444793-922082397.png"/>
     *
     *               G
     *       D               M
     *  A        F      H         Z
     *        E
     *
     */
    private TestVO buildBinaryTree() {
        TestVO tg = createTree("G");
        TestVO td = createTree("D");
        TestVO tm = createTree("M");
        TestVO ta = createTree("A");
        TestVO tf = createTree("F");
        TestVO th = createTree("H");
        TestVO tz = createTree("Z");
        TestVO te = createTree("E");

        tg.getChildren().add(td);
        tg.getChildren().add(tm);

        td.getChildren().add(ta);
        td.getChildren().add(tf);

        tm.getChildren().add(th);
        tm.getChildren().add(tz);

        tf.getChildren().add(te);

        return tg;
    }

    private TestVO createTree(String value) {
        TestVO t = new TestVO();
        t.setValue(value);
        t.setChildren(new ArrayList<>());
        return t;
    }



    @Test
    public void test_preOrder() {
        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.preOrder(root, vo -> sb.append(vo.getValue()));

        assertEquals("GDAFEMHZ", sb.toString());
    }

    @Test
    public void test_preOrderRe() {
        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.preOrderRe(root, vo -> sb.append(vo.getValue()));

        assertEquals("GDAFEMHZ", sb.toString());
    }

    @Test
    public void test_midOrder() {
        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.midOrder(root, vo -> sb.append(vo.getValue()));

        assertEquals("ADEFGHMZ", sb.toString());
    }

    @Test
    public void test_midOrderRe() {
        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.midOrderRe(root, vo -> sb.append(vo.getValue()));

        assertEquals("ADEFGHMZ", sb.toString());
    }

    @Test
    public void test_postOrder() {
        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.postOrder(root, vo -> sb.append(vo.getValue()));

        assertEquals("AEFDHZMG", sb.toString());
    }


    @Test
    public void test_postOrderRe() {
        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.postOrderRe(root, vo -> sb.append(vo.getValue()));

        assertEquals("AEFDHZMG", sb.toString());
    }


    @Test
    public void test_levelOrderRe() {
        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.levelOrder(root, vo -> sb.append(vo.getValue()));

        assertEquals("GDMAFHZE", sb.toString());
    }
}