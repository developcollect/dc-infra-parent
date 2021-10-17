package com.developcollect.core.utils;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


@Slf4j
public class ZencrUtilTest {


    @Test
    public void test_cn() {
        String baseStr = "0123456789abcdefghijklmnopqrstuvwxyz_+=?>.,;'~!@#$%^&*()<>?:\"}{][';/.,']";
        for (int i = 0; i < 10000; i++) {
            String key = RandomUtil.randomString(baseStr, RandomUtil.randomInt(1, 100));
            String value = RandomUtil.randomString(baseStr, RandomUtil.randomInt(1, 10000));
            String encrypt = ZencrUtil.encrypt(key, value);
            String decrypt = ZencrUtil.decrypt(key, encrypt);

            if (!value.equals(decrypt)) {
                log.error("解密失败：key:[{}]  value:[{}]", key, value);
            }
        }
    }


    @Test
    public void test_2() {
        String key = "这是一句中文";
        String value = "疑是银河落九天";
        String encrypt = ZencrUtil.encrypt(key, value);
        String decrypt = ZencrUtil.decrypt(key, encrypt);

        if (!value.equals(decrypt)) {
            log.error("解密失败：key:[{}]  value:[{}]", key, value);
        }
    }


    @Test
    public void test_4() {
        System.out.println(ZencrUtil.encrypt("THISISKEY", "{\n" +
                "  \"username\": \"demoData\",\n" +
                "  \"age\": 1\n" +
                "}"));
    }

}