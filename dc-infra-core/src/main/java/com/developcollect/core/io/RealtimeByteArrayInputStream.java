package com.developcollect.core.io;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

public class RealtimeByteArrayInputStream extends InputStream {

    private final Deque<byte[]> buffers = new ConcurrentLinkedDeque<>();
    private int readIndex = 0;
    private int writeIndex = 0;
    private volatile boolean over = false;
    private final Semaphore semaphore = new Semaphore(0);

    @Setter
    @Getter
    private int bufferSize = 1024;

    @Override
    public int read() throws IOException {
        byte[] first = getReadableFirst();
        if (first != null) {
            return first[readIndex++];
        } else {
            if (over) {
                return -1;
            }
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
            return read();
        }
    }

    public void over() {
        over = true;
        semaphore.release();
    }


    /**
     * 放入数据到缓存区，等待被读取
     * 该方法不支持并发
     *
     * @param bytes 要放入的数据
     */
    public void put(byte[] bytes) {
        if (over) {
            throw new RuntimeException("status is over");
        }
        int srcIndex = 0;
        do {
            byte[] last = getWriteableLast();
            int writeLen = Math.min(last.length - writeIndex, bytes.length - srcIndex);
            System.arraycopy(bytes, srcIndex, last, writeIndex, writeLen);
            srcIndex += writeLen;
            writeIndex += writeLen;
            semaphore.release();
        } while (srcIndex < bytes.length);
    }

    /**
     * 获取一个可以写入的数组，如果当前没有或者已经写满，则创建一个放在末尾
     */
    private byte[] getWriteableLast() {
        byte[] last;
        if (buffers.isEmpty()) {
            last = new byte[bufferSize];
            buffers.addLast(last);
            writeIndex = 0;
        } else {
            last = buffers.getLast();
            if (writeIndex >= last.length) {
                last = new byte[bufferSize];
                buffers.addLast(last);
                writeIndex = 0;
            }
        }
        return last;
    }


    private byte[] getReadableFirst() {
        if (buffers.isEmpty()) {
            semaphore.drainPermits();
            return null;
        }
        byte[] first = buffers.getFirst();
        if (readIndex < first.length) {
            if (buffers.size() == 1 && readIndex >= writeIndex) {
                semaphore.drainPermits();
                return null;
            }
            return first;
        } else {
            buffers.removeFirst();
            readIndex = 0;
            return getReadableFirst();
        }
    }
}
