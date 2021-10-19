package com.developcollect.core.utils;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SignUtilTest {

    public static void main(String[] args) {
        signTest();

        verifyTest();
    }


    /**
     * 签名测试
     */
    private static void signTest() {
        // rsa私钥
        String rsaPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJu27RTGVu9IJCH9R+yodLLHSH8KkkxV5elm++C2EXZbu5C6CPNqztUhkIbqRTELkDJgR/lhuCKVP05efWOGYtub+TurrCDxuqM06AM03dqz8Om0ia4WSCOjXVPyeabs821yX3Ctme2XT5D4kk4Agn7yr7KgscXJAuajXJuxeOM3AgMBAAECgYAQUq6rnNcEIlXXUku13TrOeuF4n80SP3bobqk8txlBhQA2fanuimXAJdKWsfwq1gF5pmolFD9PMMIAHxuZ0T6PiLp2RINyg5hoYeX3qHLCg4zTtthzlPHYSumo6XX4tAUTzLu5vdUmJfxm2I9iqxJYL1vii6YW4ZtEetJebS2i0QJBAN/FVfr4erKLa5duHsSdvij2DeUId0nuI5N21kwfgQ6/f8k4LnmqL4dmdpjdkLyDgZM0wrtSvYfiAKvJNMrkAqMCQQCyJEizPw0OHIMGZaM2QL8TFzTO0+xLN78L4K8rIE8U6e4KWqocIRR4mbNqlAz+DVOclvZ4wwcSxGg8oXDneTpdAkA9BcN8vWY4amzcztr1I09IPFFts/FT5+0ruayW9cBsFSzz4q5J3282rWqKJWHjBrm/OxQfoWCuPaORNT2AVs2hAkAdkB/wfzovaVPlL+DSFBShmmxNFqZUJUAzPGpVgdsd4WR7m2g3mtXG3dsEiOVPE+8YQYVNrS/zUVzrEu+lulyRAkEAj2nefO1KU+GvKBeqJ5IzNcfWUPZec85GR1GIBNUFiRLKfaiqiuDQEWcnqnYwWPbuzgplLLYo8D6KnkAf3AzvTA==";

        // 业务字段转JSON
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 22);
        map.put("Avatar", "");

        String bizContentJsonStr = JSON.toJSONString(map);

        Map<String, String> requestMap = SignUtil.signAndEncryptBizContent(bizContentJsonStr, rsaPrivateKey);

        // 输出最终请求参数
        System.out.println("最终请求参数：" + JSON.toJSONString(requestMap));
    }

    /**
     * 验签测试
     */
    private static void verifyTest() {
        // rsa公钥
        String rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbtu0UxlbvSCQh/UfsqHSyx0h/CpJMVeXpZvvgthF2W7uQugjzas7VIZCG6kUxC5AyYEf5YbgilT9OXn1jhmLbm/k7q6wg8bqjNOgDNN3as/DptImuFkgjo11T8nmm7PNtcl9wrZntl0+Q+JJOAIJ+8q+yoLHFyQLmo1ybsXjjNwIDAQAB";
        String originBody = "{\"secretKey\":\"gtkHwM+ZqIWSGzzJaLCEkcRBl2vD1YYmOHGpu/JGVBDII+Ne6FoC8bLb0IDAwFFnZhxHmdBXbqyf/Dg2y0AHK/Ovs7Cx7tf41+/0z4aU1CQ4a5wsrX4Mr/H+/1t5Fzc0BIGA4SQ8rTOK1SBogPyaqKwm+2RSk6UimILWFU/Vs94=\",\"bizContent\":\"vf47niJ4BvmRw8zjqmS+eMUE3OOXYhWEmo5CfbRDM/xEblP4hJZwnoiQPsyoVHOk\",\"sign\":\"a2FX1JqLZaJzT/B+QIxW/xpTF1wqGajPKBC6dQGC67zhZJEzwN0QAEvgFcsbB1NsPnMohEk6Lz96Iv9PQrIZCJ/TPDkNVunrrkWd9v5u209d3Q5DkfxPkZf5jrRGYX9Bww0+Ln/FY+UuaT+++lnefUYgfpqMDilrI773gnN5+Gg=\"}";

        String bizContent = SignUtil.verifySignAndDecryptBizContent(originBody, rsaPublicKey);
        System.out.println("业务报文：" + bizContent);
    }

}