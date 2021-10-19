package com.developcollect.core.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSON;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.*;

public class SignUtil {



    public static void main(String[] args) {
        generateKey();

        signTest();

        verifyTest();
    }


    /**
     * 签名测试
     */
    private static void signTest() {
        // rsa私钥
        String rsaPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJu27RTGVu9IJCH9R+yodLLHSH8KkkxV5elm++C2EXZbu5C6CPNqztUhkIbqRTELkDJgR/lhuCKVP05efWOGYtub+TurrCDxuqM06AM03dqz8Om0ia4WSCOjXVPyeabs821yX3Ctme2XT5D4kk4Agn7yr7KgscXJAuajXJuxeOM3AgMBAAECgYAQUq6rnNcEIlXXUku13TrOeuF4n80SP3bobqk8txlBhQA2fanuimXAJdKWsfwq1gF5pmolFD9PMMIAHxuZ0T6PiLp2RINyg5hoYeX3qHLCg4zTtthzlPHYSumo6XX4tAUTzLu5vdUmJfxm2I9iqxJYL1vii6YW4ZtEetJebS2i0QJBAN/FVfr4erKLa5duHsSdvij2DeUId0nuI5N21kwfgQ6/f8k4LnmqL4dmdpjdkLyDgZM0wrtSvYfiAKvJNMrkAqMCQQCyJEizPw0OHIMGZaM2QL8TFzTO0+xLN78L4K8rIE8U6e4KWqocIRR4mbNqlAz+DVOclvZ4wwcSxGg8oXDneTpdAkA9BcN8vWY4amzcztr1I09IPFFts/FT5+0ruayW9cBsFSzz4q5J3282rWqKJWHjBrm/OxQfoWCuPaORNT2AVs2hAkAdkB/wfzovaVPlL+DSFBShmmxNFqZUJUAzPGpVgdsd4WR7m2g3mtXG3dsEiOVPE+8YQYVNrS/zUVzrEu+lulyRAkEAj2nefO1KU+GvKBeqJ5IzNcfWUPZec85GR1GIBNUFiRLKfaiqiuDQEWcnqnYwWPbuzgplLLYo8D6KnkAf3AzvTA==";
        String ll = "";
        // 业务字段转JSON
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 22);
        map.put("Avatar", "");

        String bizContentJsonStr = JSON.toJSONString(map);

        Map<String, String> requestMap = signAndEncryptBizContent(bizContentJsonStr, rsaPrivateKey, "");

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

        String bizContent = verifySignAndDecryptBizContent(originBody, rsaPublicKey, "");
        System.out.println("业务报文：" + bizContent);
    }

    /**
     * 生成RSA的公钥和私钥
     */
    private static void generateKey() {

        System.out.println("己方密钥对");
        KeyPair keyPair = SecureUtil.generateKeyPair("RSA");
        System.out.println("私钥：" + Base64.encode(keyPair.getPrivate().getEncoded()));
        System.out.println("公钥：" + Base64.encode(keyPair.getPublic().getEncoded()));

        System.out.println("对方密钥对");
        KeyPair keyPair2 = SecureUtil.generateKeyPair("RSA");
        System.out.println("私钥：" + Base64.encode(keyPair2.getPrivate().getEncoded()));
        System.out.println("公钥：" + Base64.encode(keyPair2.getPublic().getEncoded()));
    }


    /**
     * 对报文进行验签并解密出业务报文
     * @param originalBody 原始报文
     * @param oppositePublicKey rsa公钥
     * @return 业务报文
     */
    public static String verifySignAndDecryptBizContent(String originalBody, String rsaPrivateKey, String oppositePublicKey) {
        // 原始报文
        Map<String, Object> bodyMap = JSON.parseObject(originalBody);
        // 获取签名，并将sign从map中移除
        String signStr = (String) bodyMap.remove("sign");
        if (signStr == null) {
            throw new RuntimeException("签名不存在");
        }

        // 生成待签字符串
        String signContent = buildSignContent(bodyMap);
        // 使用对方的RSA公钥进行验签
        Sign sign = new Sign(SignAlgorithm.SHA256withRSA, null, Base64.decode(oppositePublicKey));
        boolean verify = sign.verify(signContent.getBytes(StandardCharsets.UTF_8), Base64.decode(signStr));
        if (!verify) {
            throw new RuntimeException("验证签名失败");
        }

        // 签名验证成功后则解密业务报文
        String encryptedAesKey = (String) bodyMap.get("secretKey");
        // 使用己方的RSA私钥解密出AES秘钥
        RSA rsa = new RSA(Base64.decode(rsaPrivateKey), null);
        byte[] aesKeyBytes = rsa.decrypt(Base64.decode(encryptedAesKey), KeyType.PrivateKey);

        String encryptedBizContentStr = (String) bodyMap.get("bizContent");
        AES aes = new AES(Mode.ECB, Padding.PKCS5Padding, aesKeyBytes);
        String bizContentStr = aes.decryptStr(Base64.decode(encryptedBizContentStr));
        return bizContentStr;
    }

    /**
     * 对业务报文进行签名并加密
     * @param bizContent 业务报文
     * @param rsaPrivateKey 己方的rsa私钥
     * @param oppositePublicKey 对方的rsa公钥
     * @return 返回加密后的业务报文以及aes秘钥和签名
     */
    public static Map<String, String> signAndEncryptBizContent(String bizContent, String rsaPrivateKey, String oppositePublicKey) {
        byte[] bizContentBytes = bizContent.getBytes(StandardCharsets.UTF_8);

        // 随机生成AES秘钥
        String aesKey = RandomUtil.randomString(32);
        byte[] aesKeyBytes = aesKey.getBytes(StandardCharsets.UTF_8);

        // 对业务参数进行AES加密
        AES aes = new AES(Mode.ECB, Padding.PKCS5Padding, aesKeyBytes);
        String encryptedBizContentStr = aes.encryptBase64(bizContentBytes);

        // 使用对方的RSA公钥对AES秘钥进行加密
        RSA rsa = new RSA(null, Base64.decode(oppositePublicKey));
        String encryptedAesKey = rsa.encryptBase64(aesKeyBytes, KeyType.PublicKey);

        // 构建公共请求参数
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("bizContent", encryptedBizContentStr);
        requestParams.put("secretKey", encryptedAesKey);

        // 对公共请求参数进行签名
        Sign sign = new Sign(SignAlgorithm.SHA256withRSA, Base64.decode(rsaPrivateKey), null);
        // 生成待签字符串
        String signContent = buildSignContent(requestParams);
        // 执行签名
        String signStr = Base64.encode(sign.sign(signContent.getBytes(StandardCharsets.UTF_8)));

        // 放入签名
        requestParams.put("sign", signStr);

        // 返回最终参数
        return requestParams;
    }

    /**
     * 构建待签名字符串
     * @param paramMap 参数map
     * @return 待签名字符串
     */
    public static String buildSignContent(Map<String, ?> paramMap) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(paramMap.keySet());
        keys.sort(Comparator.naturalOrder());
        for (String key : keys) {
            // 忽略签名字段
            if ("sign".equals(key)) {
                continue;
            }
            Object paramValue = paramMap.get(key);
            // 如果值是null或者空字符串，则不参与加签
            if (paramValue == null) {
                continue;
            }
            if (paramValue instanceof CharSequence) {
                if (StrUtil.isBlank((CharSequence) paramValue)) {
                    continue;
                }
            }
            content.append(key).append("=").append(paramValue).append("&");
        }
        if (content.length() > 0) {
            content.delete(content.length() - 1, content.length());
        }
        return content.toString();
    }




}
