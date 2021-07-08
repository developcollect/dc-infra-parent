package com.developcollect.core.thread.lock;

import cn.hutool.core.thread.ThreadUtil;
import com.developcollect.core.lang.SystemClock;
import com.developcollect.core.utils.StrUtil;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

public class LockUtilTest {

    @Test
    public void testLock() {
        String key = "123";

        int threadNum = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);

        Runnable runnable = () -> {
            String name = Thread.currentThread().getName();
            System.out.println(StrUtil.format("{}: 尝试上锁", name));
            try {
                // 等待其他线程，只有所有线程同时到达这里之后才会继续往下走
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LockUtil.lock(key);
            System.out.println();
            System.out.println(StrUtil.format("{}: 上锁成功", name));

            ThreadUtil.sleep(2000);

            LockUtil.unlock(key);
            System.out.println(StrUtil.format("{}: 释放锁", name));
            countDownLatch.countDown();
        };

        for (int i = 0; i < threadNum; i++) {
            Thread t = new Thread(runnable);
            t.setName("T" + i);
            t.start();
        }


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testTryLock() {
        String key = "123";

        int threadNum = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);

        Runnable runnable = () -> {
            String name = Thread.currentThread().getName();
            boolean locked;
            try {
                // 等待其他线程，只有所有线程同时到达这里之后才会继续往下走
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            do {
                locked = LockUtil.tryLock(key);
                System.out.println(StrUtil.format("{}: 尝试上锁：{}", name, locked));
                ThreadUtil.sleep(500);
            } while (!locked);

            ThreadUtil.sleep(2000);

            LockUtil.unlock(key);
            System.out.println(StrUtil.format("{}: 释放锁", name));
            System.out.println();
            countDownLatch.countDown();
        };


        for (int i = 0; i < threadNum; i++) {
            Thread t = new Thread(runnable);
            t.setName("T" + i);
            t.start();
        }



        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testUtilReentrantLock() {

        String key = "123";

        int threadNum = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);

        Runnable runnable = () -> {
            String name = Thread.currentThread().getName();
            System.out.println(StrUtil.format("{}: 尝试上锁", name));
            try {
                // 等待其他线程，只有所有线程同时到达这里之后才会继续往下走
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LockUtil.lock(key);
            System.out.println();
            System.out.println(StrUtil.format("{}: 上锁成功", name));
            LockUtil.lock(key);
            System.out.println(StrUtil.format("{}: 重入上锁成功", name));

            ThreadUtil.sleep(2000);

            LockUtil.unlock(key);
            System.out.println(StrUtil.format("{}: 释放锁1", name));
            LockUtil.unlock(key);
            System.out.println(StrUtil.format("{}: 释放锁2", name));
            countDownLatch.countDown();
        };

        for (int i = 0; i < threadNum; i++) {
            Thread t = new Thread(runnable);
            t.setName("T" + i);
            t.start();
        }


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test_wait() {
        String key = "123";

        int threadNum = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);

        Runnable runnable = () -> {
            String name = Thread.currentThread().getName();
            try {
                // 等待其他线程，只有所有线程同时到达这里之后才会继续往下走
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            synchronized (key) {
                System.out.println(name + " " + SystemClock.now() + " ==> 上锁成功，休眠");
                try {
                    countDownLatch.countDown();
                    key.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(name + " " + SystemClock.now() + " ==> 被唤醒");
                ThreadUtil.sleep(2000);
            }
        };

        for (int i = 0; i < threadNum; i++) {
            Thread t = new Thread(runnable);
            t.setName("T" + i);
            t.start();
        }


        try {
            countDownLatch.await();
                synchronized (key) {
                    key.notify();
                    System.out.println(SystemClock.now() + "唤醒全部");
                    ThreadUtil.sleep(1000);
                }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ThreadUtil.sync(new Object());
    }

}