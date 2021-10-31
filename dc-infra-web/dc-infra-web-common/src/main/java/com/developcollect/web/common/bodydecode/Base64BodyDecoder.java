package com.developcollect.web.common.bodydecode;

import cn.hutool.core.codec.Base64;

public class Base64BodyDecoder implements BodyDecoder {

    @Override
    public byte[] decode(byte[] bytes) {
        return Base64.decode(bytes);
    }

    @Override
    public String decodeType() {
        return "base64";
    }
}
