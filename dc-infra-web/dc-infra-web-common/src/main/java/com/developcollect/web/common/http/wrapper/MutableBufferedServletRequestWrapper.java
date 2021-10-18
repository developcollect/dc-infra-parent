package com.developcollect.web.common.http.wrapper;

import cn.hutool.core.io.IoUtil;
import com.developcollect.web.common.http.MutableRequest;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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
        this.buffer = IoUtil.readBytes(request.getInputStream(), false);
    }


    @Override
    public ServletInputStream getInputStream() {
        return new BufferedServletInputStream(buffer);
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
