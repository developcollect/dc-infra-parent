package com.developcollect.core.thread.lock;

import cn.hutool.core.thread.ThreadUtil;
import com.developcollect.core.utils.StrUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class LockUtilTest {

    @Test
    public void testLock() {
        String key = "123";


        Runnable runnable = () -> {
            String name = Thread.currentThread().getName();
            System.out.println(StrUtil.format("{}: 尝试上锁", name));
            LockUtil.lock(key);
            System.out.println(StrUtil.format("{}: 上锁成功", name));

            ThreadUtil.sleep(2000);

            LockUtil.unlock(key);
            System.out.println(StrUtil.format("{}: 释放锁", name));
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.setName("t1");
        t2.setName("t2");
        t1.start();
        t2.start();


        while (true) {
            ThreadUtil.sleep(500);
        }
    }


    @Test
    public void testTryLock() {
        String key = "123";

        Runnable runnable = () -> {
            String name = Thread.currentThread().getName();
            boolean locked;
            do {
                locked = LockUtil.tryLock(key);
                System.out.println(StrUtil.format("{}: 尝试上锁：{}", name, locked));
                ThreadUtil.sleep(500);
            } while (!locked);

            ThreadUtil.sleep(2000);

            LockUtil.unlock(key);
            System.out.println(StrUtil.format("{}: 释放锁", name));
        };


        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.setName("t1");
        t2.setName("t2");
        t1.start();
        t2.start();


        while (true) {
            ThreadUtil.sleep(500);
        }
    }

}