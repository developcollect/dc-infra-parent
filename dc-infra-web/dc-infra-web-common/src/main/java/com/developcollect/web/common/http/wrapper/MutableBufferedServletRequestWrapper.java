package com.developcollect.web.common.http.wrapper;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.IoUtil;
import com.developcollect.core.utils.ArrayUtil;
import com.developcollect.core.utils.ReflectUtil;
import com.developcollect.web.common.http.MutableRequest;
import org.springframework.http.MediaType;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 缓存报文，使报文可以重复读取
 */
public class MutableBufferedServletRequestWrapper extends HttpServletRequestWrapper implements MutableRequest {

    private volatile byte[] buffer;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public MutableBufferedServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType())) {
            // 触发Parameter解析
            request.getParameterMap();
            if ("org.apache.catalina.connector.RequestFacade".equals(request.getClass().getName())) {
                bufferRequestFacadeUrlEncodedBody(request);
            }
        } else {
            this.buffer = IoUtil.readBytes(request.getInputStream(), false);
        }
        if (this.buffer == null) {
            throw new UtilException("无法读取body");
        }
    }


    private void bufferRequestFacadeUrlEncodedBody(HttpServletRequest request) {
        try {
            Object tr = ReflectUtil.getFieldValue(request, "request");
            Object postData = ReflectUtil.getFieldValue(tr, "postData");
            this.buffer = ArrayUtil.sub((byte[]) postData, 0, request.getContentLength());
        } catch (Exception ignore) {
        }
    }


    @Override
    public ServletInputStream getInputStream() {
        return new BufferedServletInputStream(buffer);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public void setBody(byte[] bytes) {
        this.buffer = bytes;
    }


    private static class BufferedServletInputStream extends ServletInputStream {
        private ByteArrayInputStream inputStream;

        public BufferedServletInputStream(byte[] buffer) {
            this.inputStream = new ByteArrayInputStream(buffer);
        }

        @Override
        public int available() {
            return inputStream.available();
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) {
            return inputStream.read(b, off, len);
        }
    }
}
