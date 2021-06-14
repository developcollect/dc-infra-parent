package com.developcollect.core.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class StrUtilTest {


    @Test
    public void testLeft() {
        Assert.assertEquals("", StrUtil.left("haifeisi", 0));
        Assert.assertEquals("h", StrUtil.left("haifeisi", 1));
        Assert.assertEquals("ha", StrUtil.left("haifeisi", 2));
        Assert.assertEquals("hai", StrUtil.left("haifeisi", 3));
        Assert.assertEquals("haif", StrUtil.left("haifeisi", 4));
        Assert.assertEquals("haife", StrUtil.left("haifeisi", 5));
        Assert.assertEquals("haifei", StrUtil.left("haifeisi", 6));
        Assert.assertEquals("haifeis", StrUtil.left("haifeisi", 7));
        Assert.assertEquals("haifeisi", StrUtil.left("haifeisi", 8));
        Assert.assertEquals("haifeisi", StrUtil.left("haifeisi", 9));
    }

    @Test
    public void testRight() {
        Assert.assertEquals("", StrUtil.right("haifeisi", 0));
        Assert.assertEquals("i", StrUtil.right("haifeisi", 1));
        Assert.assertEquals("si", StrUtil.right("haifeisi", 2));
        Assert.assertEquals("isi", StrUtil.right("haifeisi", 3));
        Assert.assertEquals("eisi", StrUtil.right("haifeisi", 4));
        Assert.assertEquals("feisi", StrUtil.right("haifeisi", 5));
        Assert.assertEquals("ifeisi", StrUtil.right("haifeisi", 6));
        Assert.assertEquals("aifeisi", StrUtil.right("haifeisi", 7));
        Assert.assertEquals("haifeisi", StrUtil.right("haifeisi", 8));
        Assert.assertEquals("haifeisi", StrUtil.right("haifeisi", 9));
    }
}