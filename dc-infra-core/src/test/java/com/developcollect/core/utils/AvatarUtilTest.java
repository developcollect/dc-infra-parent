package com.developcollect.core.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class AvatarUtilTest {


    @Test
    public void test_avatar() {
        String avatar = AvatarUtil.createBase64Avatar(Math.abs("springboot.io".hashCode()));
        System.out.println(AvatarUtil.BASE64_PREFIX + avatar);
    }
}