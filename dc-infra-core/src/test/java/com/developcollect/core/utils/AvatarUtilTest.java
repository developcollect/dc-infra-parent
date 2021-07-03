package com.developcollect.core.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.RandomUtil;
import org.junit.Test;
import sun.security.util.ArrayUtil;

import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class AvatarUtilTest {


    @Test
    public void test_avatar() {

        for (int i = 0; i < 100; i++) {
            String s = RandomUtil.randomString(11);
            int code = s.hashCode();
            BufferedImage img = AvatarUtil.createImg(code);
            ImgUtil.write(img, FileUtil.touch("~/tmp/" + code + ".png"));
        }

    }

    @Test
    public void test_avatar3() {

        BufferedImage img = AvatarUtil.createImg(1409886);
        ImgUtil.write(img, FileUtil.touch("~/tmp/" + 1409886 + ".png"));


        BufferedImage img2 = AvatarUtil.createImg(-1409886);
        ImgUtil.write(img2, FileUtil.touch("~/tmp/" + -1409886 + ".png"));
    }
}