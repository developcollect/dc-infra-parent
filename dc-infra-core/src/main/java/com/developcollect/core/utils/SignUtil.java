package com.developcollect.core.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSON;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;


/**
 * 报文签名和验签工具类
 */
public class SignUtil {


    /**
     * 对业务报文进行签名并加密
     * 使用对方的公钥加密AES秘钥
     *
     * @param bizContent        业务报文
     * @param rsaPrivateKey     己方的rsa私钥
     * @param oppositePublicKey 对方的rsa公钥
     * @return 返回加密后的业务报文以及aes秘钥和签名
     */
    public static Map<String, String> signAndEncryptBizContent(String bizContent, String rsaPrivateKey, String oppositePublicKey) {
        // 使用对方的RSA公钥对AES秘钥进行加密
        return doSignAndEncryptBizContent(bizContent, Base64.decode(rsaPrivateKey), aesKeyBytes -> {
            RSA rsa = new RSA(null, Base64.decode(oppositePublicKey));
            return rsa.encryptBase64(aesKeyBytes, KeyType.PublicKey);
        });
    }

    /**
     * 对业务报文进行签名并加密
     * 使用己方的私钥加密AES秘钥
     *
     * @param bizContent    业务报文
     * @param rsaPrivateKey 己方的rsa私钥
     * @return 返回加密后的业务报文以及aes秘钥和签名
     */
    public static Map<String, String> signAndEncryptBizContent(String bizContent, String rsaPrivateKey) {
        byte[] rsaPrivateKeyBytes = Base64.decode(rsaPrivateKey);

        // 使用己方的RSA私钥加密AES秘钥
        return doSignAndEncryptBizContent(bizContent, rsaPrivateKeyBytes, aesKeyBytes -> {
            RSA rsa = new RSA(rsaPrivateKeyBytes, null);
            return rsa.encryptBase64(aesKeyBytes, KeyType.PrivateKey);
        });
    }

    private static Map<String, String> doSignAndEncryptBizContent(String bizContent, byte[] rsaPrivateKeyBytes, Function<byte[], String> aesKeyEncryptor) {
        byte[] bizContentBytes = bizContent.getBytes(StandardCharsets.UTF_8);

        // 随机生成AES秘钥
        String aesKey = RandomUtil.randomString(32);
        byte[] aesKeyBytes = aesKey.getBytes(StandardCharsets.UTF_8);

        // 对业务参数进行AES加密
        AES aes = new AES(Mode.ECB, Padding.PKCS5Padding, aesKeyBytes);
        String encryptedBizContentStr = aes.encryptBase64(bizContentBytes);

        // 对AES秘钥进行加密
        String encryptedAesKey = aesKeyEncryptor.apply(aesKeyBytes);

        // 构建公共请求参数
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("bizContent", encryptedBizContentStr);
        requestParams.put("secretKey", encryptedAesKey);

        // 对公共请求参数进行签名
        Sign sign = new Sign(SignAlgorithm.SHA256withRSA, rsaPrivateKeyBytes, null);
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
     * 对报文进行验签并解密出业务报文
     * 使用己方的rsa私钥解密AES秘钥
     *
     * @param originalBody      原始报文
     * @param rsaPrivateKey     己方的rsa私钥
     * @param oppositePublicKey 对方的rsa公钥
     * @return 业务报文
     */
    public static String verifySignAndDecryptBizContent(String originalBody, String rsaPrivateKey, String oppositePublicKey) {
        byte[] oppositePublicKeyBytes = Base64.decode(oppositePublicKey);
        // 使用己方的私钥解密AES秘钥
        return doVerifySignAndDecryptBizContent(originalBody, oppositePublicKeyBytes, encryptedAesKey -> {
            RSA rsa = new RSA(Base64.decode(rsaPrivateKey), null);
            return rsa.decrypt(encryptedAesKey, KeyType.PrivateKey);
        });
    }

    /**
     * 对报文进行验签并解密出业务报文
     * 使用对方的rsa公钥解密AES秘钥
     *
     * @param originalBody      原始报文
     * @param oppositePublicKey 对方的rsa公钥
     * @return 业务报文
     */
    public static String verifySignAndDecryptBizContent(String originalBody, String oppositePublicKey) {
        byte[] oppositePublicKeyBytes = Base64.decode(oppositePublicKey);
        // 使用对方的公钥解密AES秘钥
        return doVerifySignAndDecryptBizContent(originalBody, oppositePublicKeyBytes, encryptedAesKey -> {
            RSA rsa = new RSA(null, oppositePublicKeyBytes);
            return rsa.decrypt(encryptedAesKey, KeyType.PublicKey);
        });
    }


    private static String doVerifySignAndDecryptBizContent(String originalBody, byte[] oppositePublicKeyBytes, Function<String, byte[]> aesKeyDecryptor) {
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
        Sign sign = new Sign(SignAlgorithm.SHA256withRSA, null, oppositePublicKeyBytes);
        boolean verify = sign.verify(signContent.getBytes(StandardCharsets.UTF_8), Base64.decode(signStr));
        if (!verify) {
            throw new RuntimeException("验证签名失败");
        }

        // 签名验证成功后则解密业务报文
        String encryptedAesKey = (String) bodyMap.get("secretKey");
        // 解密出AES秘钥
        byte[] aesKeyBytes = aesKeyDecryptor.apply(encryptedAesKey);

        String encryptedBizContentStr = (String) bodyMap.get("bizContent");
        AES aes = new AES(Mode.ECB, Padding.PKCS5Padding, aesKeyBytes);
        String bizContentStr = aes.decryptStr(Base64.decode(encryptedBizContentStr));
        return bizContentStr;
    }


    /**
     * 构建待签名字符串
     *
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
