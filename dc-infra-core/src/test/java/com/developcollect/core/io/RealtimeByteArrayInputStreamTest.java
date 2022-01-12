package com.developcollect.core.io;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ByteUtil;
import com.developcollect.core.thread.ThreadUtil;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class RealtimeByteArrayInputStreamTest {


    @Test
    public void test_put() {
        RealtimeByteArrayInputStream inputStream = new RealtimeByteArrayInputStream();

        ThreadUtil.execAsync(() -> {
            for (int i = 1; i <= 30; i++) {
                inputStream.put("RealtimeByteArrayInputStreamTest\n".getBytes());
                System.out.println("EX: " + i);
                ThreadUtil.sleep(100);
            }
            inputStream.over();
            System.out.println("over");
        });


        FileOutputStream out = null;
        try {
            out = new FileOutputStream(FileUtil.touch("D:\\laboratory\\33.txt"));
            IoUtil.copy(inputStream, out);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            IoUtil.close(out);
            IoUtil.close(inputStream);
        }

    }
}