package com.developcollect.web.common.bodydecode;

import cn.hutool.core.io.IoUtil;
import com.developcollect.web.common.http.MutableRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface BodyDecoder {

    String decodeType();

    default void decode(HttpServletRequest httpServletRequest, MutableRequest mutableRequest) throws IOException {
        byte[] originBody = IoUtil.readBytes(httpServletRequest.getInputStream());
        mutableRequest.setBody(decode(originBody));
    }

    byte[] decode(byte[] bytes);
}
