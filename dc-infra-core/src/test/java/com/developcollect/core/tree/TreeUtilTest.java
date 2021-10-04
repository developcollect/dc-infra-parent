package com.developcollect.core.tree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    /**
     *                      A
     *      B               C             D
     * e  f  g  h          ijk           lm
     *   nz
     * @return
     */
    private TestVO buildTree() {
        TestVO ta = createTree("A");
        TestVO tb = createTree("B");
        TestVO tc = createTree("C");
        TestVO td = createTree("D");
        TestVO te = createTree("E");
        TestVO tf = createTree("F");
        TestVO tg = createTree("G");
        TestVO th = createTree("H");
        TestVO ti = createTree("I");
        TestVO tj = createTree("J");
        TestVO tk = createTree("K");
        TestVO tl = createTree("L");
        TestVO tm = createTree("M");
        TestVO tn = createTree("N");
        TestVO tz = createTree("Z");

        ta.getChildren().add(tb);
        ta.getChildren().add(tc);
        ta.getChildren().add(td);


        tb.getChildren().add(te);
        tb.getChildren().add(tf);
        tb.getChildren().add(tg);
        tb.getChildren().add(th);

        tc.getChildren().add(ti);
        tc.getChildren().add(tj);
        tc.getChildren().add(tk);

        td.getChildren().add(tl);
        td.getChildren().add(tm);


        tf.getChildren().add(tn);
        tf.getChildren().add(tz);

        return ta;
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
        StringBuilder sb1 = new StringBuilder();
        TreeUtil.preOrder(root, vo -> sb1.append(vo.getValue()));
        assertEquals("GDAFEMHZ", sb1.toString());

        TestVO t2 = buildTree();
        StringBuilder sb2 = new StringBuilder();
        TreeUtil.preOrder(t2, vo -> sb2.append(vo.getValue()));
        assertEquals("ABEFNZGHCIJKDLM", sb2.toString());
    }

    @Test
    public void test_preOrderRe() {
        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.preOrderRe(root, vo -> sb.append(vo.getValue()));
        assertEquals("GDAFEMHZ", sb.toString());

        TestVO t2 = buildTree();
        StringBuilder sb2 = new StringBuilder();
        TreeUtil.preOrder(t2, vo -> sb2.append(vo.getValue()));
        assertEquals("ABEFNZGHCIJKDLM", sb2.toString());
    }

    @Test
    public void test_midOrder() {
        TestVO t2 = buildTree();
        StringBuilder sb2 = new StringBuilder();
        TreeUtil.midOrder(t2, vo -> sb2.append(vo.getValue()));
        assertEquals("EBHALDM", sb2.toString());

        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.midOrder(root, vo -> sb.append(vo.getValue()));
        assertEquals("ADEFGHMZ", sb.toString());
    }

    @Test
    public void test_midOrderRe() {
        TestVO t2 = buildTree();
        StringBuilder sb2 = new StringBuilder();
        TreeUtil.midOrderRe(t2, vo -> sb2.append(vo.getValue()));
        assertEquals("EBHALDM", sb2.toString());

        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.midOrderRe(root, vo -> sb.append(vo.getValue()));
        assertEquals("ADEFGHMZ", sb.toString());
    }

    @Test
    public void test_postOrder() {
        TestVO t2 = buildTree();
        StringBuilder sb2 = new StringBuilder();
        TreeUtil.postOrder(t2, vo -> sb2.append(vo.getValue()));
        assertEquals("ENZFGHBIJKCLMDA", sb2.toString());

        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.postOrder(root, vo -> sb.append(vo.getValue()));
        assertEquals("AEFDHZMG", sb.toString());
    }


    @Test
    public void test_postOrderRe() {
        TestVO t2 = buildTree();
        StringBuilder sb2 = new StringBuilder();
        TreeUtil.postOrderRe(t2, vo -> sb2.append(vo.getValue()));
        assertEquals("ENZFGHBIJKCLMDA", sb2.toString());


        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.postOrderRe(root, vo -> sb.append(vo.getValue()));
        assertEquals("AEFDHZMG", sb.toString());
    }


    @Test
    public void test_levelOrderRe() {
        TestVO t2 = buildTree();
        StringBuilder sb2 = new StringBuilder();
        TreeUtil.levelOrder(t2, vo -> sb2.append(vo.getValue()));
        assertEquals("ABCDEFGHIJKLMNZ", sb2.toString());


        TestVO root = buildBinaryTree();
        StringBuilder sb = new StringBuilder();
        TreeUtil.levelOrder(root, vo -> sb.append(vo.getValue()));
        assertEquals("GDMAFHZE", sb.toString());
    }
}