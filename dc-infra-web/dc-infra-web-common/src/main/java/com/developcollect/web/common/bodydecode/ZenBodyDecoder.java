package com.developcollect.web.common.bodydecode;

import cn.hutool.crypto.CryptoException;
import com.developcollect.core.utils.ZencrUtil;

import java.nio.charset.StandardCharsets;

public class ZenBodyDecoder implements BodyDecoder {
    private String key;

    public ZenBodyDecoder(String key) {
        this.key = key;
    }

    @Override
    public byte[] decode(byte[] bytes) {
        try {
            return ZencrUtil.decrypt(key, new String(bytes, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
        } catch (CryptoException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String decodeType() {
        return "zen";
    }
}
