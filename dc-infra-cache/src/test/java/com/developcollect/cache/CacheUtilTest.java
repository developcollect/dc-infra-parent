package com.developcollect.cache;

import com.developcollect.core.thread.ThreadUtil;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CacheUtilTest {

    @Test
    public void test1() {
        String v = CacheUtil.get("dd");
        Assert.assertNull(v);
    }


    @Test
    public void test2() {
        CacheUtil.set("dd", "23424");
        String v = CacheUtil.get("dd");
        Assert.assertEquals("23424", v);
    }

    @Test
    public void test3() {
        CacheUtil.setAndRecordTime("dd", "23424");
        for (int i = 0; i < 10; i++) {
            System.out.println(CacheUtil.getAge("dd"));
            ThreadUtil.sleep(1000);
        }
    }
}