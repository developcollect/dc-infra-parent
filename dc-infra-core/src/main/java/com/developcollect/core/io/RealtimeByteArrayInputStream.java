package com.developcollect.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class RealtimeByteArrayInputStream extends InputStream {

    private final Deque<byte[]> buffers = new ArrayDeque<>();
    // The index in the byte[] found at buffers.getLast() to be written next
    private int index = 0;
    private volatile boolean over = false;
    private final Object lock = new Object();

    @Override
    public int read() throws IOException {
        if (over) {
            return -1;
        }
        if (end()) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ignore) {}
            }
            return read();
        }
        return 0;
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
     * @param bytes 要放入的数据
     */
    public void put(byte[] bytes) {
        // todo 加入到buffer中

    }

}
