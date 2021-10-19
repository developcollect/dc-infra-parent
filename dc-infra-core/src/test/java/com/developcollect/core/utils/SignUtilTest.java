package com.developcollect.core.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SignUtilTest {


    /**
     * 只使用一对秘钥进行验签和加密
     */
    @Test
    public void test_sign_v() {
        // rsa私钥
        String rsaPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJu27RTGVu9IJCH9R+yodLLHSH8KkkxV5elm++C2EXZbu5C6CPNqztUhkIbqRTELkDJgR/lhuCKVP05efWOGYtub+TurrCDxuqM06AM03dqz8Om0ia4WSCOjXVPyeabs821yX3Ctme2XT5D4kk4Agn7yr7KgscXJAuajXJuxeOM3AgMBAAECgYAQUq6rnNcEIlXXUku13TrOeuF4n80SP3bobqk8txlBhQA2fanuimXAJdKWsfwq1gF5pmolFD9PMMIAHxuZ0T6PiLp2RINyg5hoYeX3qHLCg4zTtthzlPHYSumo6XX4tAUTzLu5vdUmJfxm2I9iqxJYL1vii6YW4ZtEetJebS2i0QJBAN/FVfr4erKLa5duHsSdvij2DeUId0nuI5N21kwfgQ6/f8k4LnmqL4dmdpjdkLyDgZM0wrtSvYfiAKvJNMrkAqMCQQCyJEizPw0OHIMGZaM2QL8TFzTO0+xLN78L4K8rIE8U6e4KWqocIRR4mbNqlAz+DVOclvZ4wwcSxGg8oXDneTpdAkA9BcN8vWY4amzcztr1I09IPFFts/FT5+0ruayW9cBsFSzz4q5J3282rWqKJWHjBrm/OxQfoWCuPaORNT2AVs2hAkAdkB/wfzovaVPlL+DSFBShmmxNFqZUJUAzPGpVgdsd4WR7m2g3mtXG3dsEiOVPE+8YQYVNrS/zUVzrEu+lulyRAkEAj2nefO1KU+GvKBeqJ5IzNcfWUPZec85GR1GIBNUFiRLKfaiqiuDQEWcnqnYwWPbuzgplLLYo8D6KnkAf3AzvTA==";
        // rsa公钥
        String rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbtu0UxlbvSCQh/UfsqHSyx0h/CpJMVeXpZvvgthF2W7uQugjzas7VIZCG6kUxC5AyYEf5YbgilT9OXn1jhmLbm/k7q6wg8bqjNOgDNN3as/DptImuFkgjo11T8nmm7PNtcl9wrZntl0+Q+JJOAIJ+8q+yoLHFyQLmo1ybsXjjNwIDAQAB";


        // 业务字段转JSON
        String bizContentJsonStr = JSON.toJSONString(getBizContentMap());
        // 加签
        Map<String, String> requestMap = SignUtil.signAndEncryptBizContent(bizContentJsonStr, rsaPrivateKey);
        String signedBody = JSON.toJSONString(requestMap);
        // 输出最终请求参数
        System.out.println("最终请求参数：" + signedBody);

        String bizContent = SignUtil.verifySignAndDecryptBizContent(signedBody, rsaPublicKey);
        System.out.println("业务报文：" + bizContent);
        JSONObject bizContentMap = JSON.parseObject(bizContent);
        assertBizContentMap(bizContentMap);
    }


    /**
     * 使用两对秘钥进行验签和加密
     */
    @Test
    public void test_sign_v2() {
        // 己方秘钥对
        String rsaPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALnkTriEDvzR5LchtX0RQ3UXRzhT5B4u6uu/wQ532CZtzUHfDZqcJ6FKriSJ8b284Mcoyt7sNE+jA4CLhxgm0q09I7KC9xOaK84oZH7stVfNhT4Syn7B9FgarAAmwoy+zUIY0Amv5iE9EeMdwgDlgXggvMfp7cPznzjn6jMv3GszAgMBAAECgYBTT9toHueIr5lN6jGox9/vzmsA97g5PtXSqrIfhd69+mAPsaQrMukgBJRfk+NgIhKrr8CcgEhBmNGs/tuhG2U3sas6WoP78dEQsDTgWbTTDx7lhLwDbbZVAbdLPpp4jep2XyKcjjln8fvfM8xHkhoIxMgJ/3PK+hmSE/F8Uiq4YQJBAPvdGaEcbQEQ2IGuL5KmJPPz1hltj21gmaCuMU17/SrEVprs06rvlBCUI5I/E3k9kg2xSU2AO0B9j0/Iv/xG5X8CQQC88dg/i7vrKhF+62/AgwrK6I70S2W15k+lbaP8Q2VJMTgbY/+FYbyXzTDjTSOdRNCk1eK+DiCU+Fkd8G4dUJxNAkBsxzx3ggBuMmlf8OwpCUrydgClMNV+ZvdTF4jBr2hTXUMOXHS64ghv9Js8YbO2bGo1Mbm4GEb+/nuz+L86ZSBfAkEAk2UCskfal9Bgh6c4rXBMmg+jfVXYCXeaSE3osCEPQXLA9zUiHzYofi9W5OrHTqdhMEWeNW8l4bB/LMjjzcCVvQJBAK9qgDzdOmTM1hAsRtaHERt2GFwqj0IBQEhYcwn6ed2wEkkRqZRK639b/KT13yN5jYTfCPNN+VKNkWvDsH1qrAE=";
        String rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC55E64hA780eS3IbV9EUN1F0c4U+QeLurrv8EOd9gmbc1B3w2anCehSq4kifG9vODHKMre7DRPowOAi4cYJtKtPSOygvcTmivOKGR+7LVXzYU+Esp+wfRYGqwAJsKMvs1CGNAJr+YhPRHjHcIA5YF4ILzH6e3D85845+ozL9xrMwIDAQAB";
        // 对方秘钥对
        String oppositePrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ+pvbzyJzQwEL8QrD/SfokHUqXSvW9GGSxNGierTbsg1LRjzu/Nf1iWXVHMcpHn07KEdW++IGDwuI4kFUOxHYrXf+N3CtTvzf/nmzybdP8cCtJMLEqpxFDWrxqIZHuoUZpQPyTfo3QqrJkFG+A5L1XjpMwI0xPY3ouYZbqwtMwNAgMBAAECgYBeTc7A7DJVs9IgWEO1CqosW4PoZ7n2IGw762p4ZcjqrzCTP6R5pWIDPATwjJZbMZeriGsozh6nw2bupcddjR6J9LN86/iCxQzmp2ipNYtzJLgo+JlYW6Ss/rmji4s++aMSTAoIKx8Fv5FJx8Jp0ztgVN91MlNfXRXmBM6xGoxBbQJBANtHXaYhkErbzmYN40PNrAzPwGil5ur1g4933qJFAJGWF5KM7aI1gTzCYvUGEqb24xoStu4jCIJPbjWTmuz51qsCQQC6ZpuT8F9pbw6oYl01vEjXMWcvG6RpZsm1/KFjQDUUYLVfchqHq7ndV2I6JTpzYyQeZxnmgqo0D8JH9Xk6z0gnAkEA1UXeQf0XGJgdcYlEZ+gc1QEjhSUHKbPNV7cg5Pb0DKXNG47SehhMMMpn8SUM8zPhwrQ9E/UjC4syCA4eeyqsrQJBALUP7pfpqgSUaDyQL9J+rpK53m0YAMCRo+YvG+V4/RZqSkQIZlTbLpQeyb+JYhmrd+5AYBxVWFdx51pWzAegSYkCQD3dPHrxesf0QNKrQyNAhXdW3d4Wd6KYH99mRp/ExnbbPHqwafSFOAdOnq8UZglQFLQq+wl2jd/3Vhhk+hDLmMc=";
        String oppositePublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfqb288ic0MBC/EKw/0n6JB1Kl0r1vRhksTRonq027INS0Y87vzX9Yll1RzHKR59OyhHVvviBg8LiOJBVDsR2K13/jdwrU783/55s8m3T/HArSTCxKqcRQ1q8aiGR7qFGaUD8k36N0KqyZBRvgOS9V46TMCNMT2N6LmGW6sLTMDQIDAQAB";


        // 业务字段转JSON
        String bizContentJsonStr = JSON.toJSONString(getBizContentMap());
        // 加签并加密
        Map<String, String> requestMap = SignUtil.signAndEncryptBizContent(bizContentJsonStr, rsaPrivateKey, oppositePublicKey);
        String signedBody = JSON.toJSONString(requestMap);
        // 输出最终请求参数
        System.out.println("最终请求参数：" + signedBody);

        // 验签并解密
        String bizContent = SignUtil.verifySignAndDecryptBizContent(signedBody, oppositePrivateKey, rsaPublicKey);
        System.out.println("业务报文：" + bizContent);
        JSONObject bizContentMap = JSON.parseObject(bizContent);
        assertBizContentMap(bizContentMap);
    }


    @Test
    public void genKey() {
        KeyPair keyPair = SecureUtil.generateKeyPair("RSA");
        System.out.println("己方秘钥对");
        System.out.println("私钥：" + Base64.encode(keyPair.getPrivate().getEncoded()));
        System.out.println("公钥：" + Base64.encode(keyPair.getPublic().getEncoded()));

        KeyPair keyPair2 = SecureUtil.generateKeyPair("RSA");
        System.out.println("对方秘钥对");
        System.out.println("私钥：" + Base64.encode(keyPair2.getPrivate().getEncoded()));
        System.out.println("公钥：" + Base64.encode(keyPair2.getPublic().getEncoded()));
    }


    private Map<String, Object> getBizContentMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 22);
        map.put("Avatar", "");
        return map;
    }

    private void assertBizContentMap(Map<String, Object> bizContentMap) {
        assertEquals(3, bizContentMap.size());
        assertEquals("张三", bizContentMap.get("name"));
        assertEquals(22, bizContentMap.get("age"));
        assertEquals("", bizContentMap.get("Avatar"));
    }


    @Test
    public void test_2() {
        // rsa私钥
        String rsaPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJu27RTGVu9IJCH9R+yodLLHSH8KkkxV5elm++C2EXZbu5C6CPNqztUhkIbqRTELkDJgR/lhuCKVP05efWOGYtub+TurrCDxuqM06AM03dqz8Om0ia4WSCOjXVPyeabs821yX3Ctme2XT5D4kk4Agn7yr7KgscXJAuajXJuxeOM3AgMBAAECgYAQUq6rnNcEIlXXUku13TrOeuF4n80SP3bobqk8txlBhQA2fanuimXAJdKWsfwq1gF5pmolFD9PMMIAHxuZ0T6PiLp2RINyg5hoYeX3qHLCg4zTtthzlPHYSumo6XX4tAUTzLu5vdUmJfxm2I9iqxJYL1vii6YW4ZtEetJebS2i0QJBAN/FVfr4erKLa5duHsSdvij2DeUId0nuI5N21kwfgQ6/f8k4LnmqL4dmdpjdkLyDgZM0wrtSvYfiAKvJNMrkAqMCQQCyJEizPw0OHIMGZaM2QL8TFzTO0+xLN78L4K8rIE8U6e4KWqocIRR4mbNqlAz+DVOclvZ4wwcSxGg8oXDneTpdAkA9BcN8vWY4amzcztr1I09IPFFts/FT5+0ruayW9cBsFSzz4q5J3282rWqKJWHjBrm/OxQfoWCuPaORNT2AVs2hAkAdkB/wfzovaVPlL+DSFBShmmxNFqZUJUAzPGpVgdsd4WR7m2g3mtXG3dsEiOVPE+8YQYVNrS/zUVzrEu+lulyRAkEAj2nefO1KU+GvKBeqJ5IzNcfWUPZec85GR1GIBNUFiRLKfaiqiuDQEWcnqnYwWPbuzgplLLYo8D6KnkAf3AzvTA==";
        // rsa公钥
        String rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbtu0UxlbvSCQh/UfsqHSyx0h/CpJMVeXpZvvgthF2W7uQugjzas7VIZCG6kUxC5AyYEf5YbgilT9OXn1jhmLbm/k7q6wg8bqjNOgDNN3as/DptImuFkgjo11T8nmm7PNtcl9wrZntl0+Q+JJOAIJ+8q+yoLHFyQLmo1ybsXjjNwIDAQAB";

        String content = "五类晒";
        RSA rsa = SecureUtil.rsa(rsaPrivateKey, rsaPublicKey);


        // 私钥加密，公钥解密
        System.out.println(new String(rsa.decrypt(rsa.encrypt(content, KeyType.PrivateKey), KeyType.PublicKey)));

        // 公钥加密，私钥解密
        System.out.println(new String(rsa.decrypt(rsa.encrypt(content, KeyType.PublicKey), KeyType.PrivateKey)));


    }
}