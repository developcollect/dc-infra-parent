package com.developcollect.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;

public class RealtimeByteArrayInputStream extends InputStream {

    private final Deque<byte[]> buffers = new ArrayDeque<>();
    // The index in the byte[] found at buffers.getFirst() to be written next
    private int readIndex = 0;
    private volatile boolean over = false;
    private final Semaphore semaphore = new Semaphore(0);

    @Override
    public int read() throws IOException {
        if (over) {
            return -1;
        }
        if (end()) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
            return read();
        }
        return buffers.getFirst()[readIndex++];
    }

    public void over() {
        over = true;
    }


    private boolean end() {
        return true;
    }


    /**
     * 放入数据到缓存区，等待被读取
     * 该方法不支持并发
     *
     * @param bytes 要放入的数据
     */
    public void put(byte[] bytes) {
        // todo 加入到buffer中
        semaphore.release();
    }

}
