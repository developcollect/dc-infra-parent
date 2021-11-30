package com.developcollect.web.common.http.wrapper;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import com.developcollect.web.common.http.MutableResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.Optional;

public class MutableBufferedServletResponseWrapper extends HttpServletResponseWrapper implements MutableResponse {

    private int httpStatus = HttpServletResponse.SC_OK;
    private PrintWriter bufferedWriter;
    private BufferedServletOutputStream bufferedStream;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public MutableBufferedServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }


    @Override
    public ServletOutputStream getOutputStream() {
        if (bufferedWriter != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }
        if (bufferedStream == null) {
            bufferedStream = new BufferedServletOutputStream();
        }
        return bufferedStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (bufferedWriter == null) {
            bufferedWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), getResponse().getCharacterEncoding()), true);
        }
        return bufferedWriter;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (bufferedWriter != null) {
            bufferedWriter.flush();
        } else if (bufferedStream != null) {
            bufferedStream.flush();
        }
    }

    @Override
    public void sendError(int sc) throws IOException {
        httpStatus = sc;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        httpStatus = sc;
        getWriter().write(msg);
    }

    @Override
    public void setStatus(int sc) {
        httpStatus = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        httpStatus = sc;
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStatus() {
        return httpStatus;
    }


    @Override
    public void setBody(byte[] bytes) {
        if (bufferedStream == null) {
            // 触发 bufferedStream 实例化
            getOutputStream();
        }
        bufferedStream.setBufferedBytes(bytes);
    }

    @Override
    public byte[] getBody() {
        return Optional.ofNullable(bufferedStream)
                .map(BufferedServletOutputStream::getBufferedBytes)
                .orElseGet(() -> new byte[0]);
    }


    /**
     * 真正处理输出
     */
    public void processOutbound() throws IOException {
        this.flushBuffer();
        HttpServletResponse nativeResponse = (HttpServletResponse) getResponse();
        nativeResponse.setStatus(this.getStatus());
        ServletOutputStream outputStream = nativeResponse.getOutputStream();
        outputStream.write(this.getBody());
        outputStream.flush();
        outputStream.close();
    }


    private static class BufferedServletOutputStream extends ServletOutputStream {

        private final FastByteArrayOutputStream arrayOutputStream;

        public BufferedServletOutputStream() {
            this.arrayOutputStream = new FastByteArrayOutputStream();
        }

        @Override
        public void write(int b) throws IOException {
            this.arrayOutputStream.write(b);
        }

        public byte[] getBufferedBytes() {
            return arrayOutputStream.toByteArray();
        }

        public void setBufferedBytes(byte[] bytes) {
            reset();
            IoUtil.write(arrayOutputStream, false, bytes);
        }

        public void reset() {
            arrayOutputStream.reset();
        }

        @Override
        public void close() throws IOException {
            this.arrayOutputStream.close();
        }
    }

}
