package com.developcollect.core.thread.lock;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.developcollect.core.lang.SystemClock;
import com.developcollect.core.utils.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Slf4j
public class LockUtilTest {

    @Test
    public void testLock() {
        String key = "123";

        int threadNum = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);

        Runnable runnable = () -> {
            CacheLock lock = LockUtil.createCacheLock(key);
            String name = Thread.currentThread().getName();
            System.out.println(StrUtil.format("{}: 尝试上锁", name));
            try {
                // 等待其他线程，只有所有线程同时到达这里之后才会继续往下走
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            lock.lock();
            System.out.println();
            System.out.println(StrUtil.format("{}: 上锁成功", name));

            ThreadUtil.sleep(2000);

            lock.unlock();
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
    public void testTryLockTimeout() {
        String key = "123";

        int threadNum = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);

        Runnable runnable = () -> {
            CacheLock lock = LockUtil.createCacheLock(key);
            String name = Thread.currentThread().getName();
            int timeout = RandomUtil.randomInt(1, 8);
            try {
                // 等待其他线程，只有所有线程同时到达这里之后才会继续往下走
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(StrUtil.format("{} ==> {}: 尝试上锁, 超时: {}s", System.currentTimeMillis(), name, timeout));
            boolean locked = false;
            try {
                locked = lock.tryLock(timeout, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("获取锁时线程中断");
            }
            System.out.println(StrUtil.format("{} ==> {}: 获取锁 {}", System.currentTimeMillis(), name, locked));

            ThreadUtil.sleep(RandomUtil.randomLong(2000, 4000));

            if (locked) {
                lock.unlock();
                System.out.println(StrUtil.format("{} ==> {}: 释放锁", System.currentTimeMillis(), name));
            }

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
    public void testTryLock() {
        String key = "123";

        int threadNum = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);

        Runnable runnable = () -> {
            CacheLock lock = LockUtil.createCacheLock(key);
            String name = Thread.currentThread().getName();
            boolean locked;
            try {
                // 等待其他线程，只有所有线程同时到达这里之后才会继续往下走
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            do {
                locked = lock.tryLock();
                System.out.println(StrUtil.format("{}: 尝试上锁：{}", name, locked));
                ThreadUtil.sleep(500);
            } while (!locked);

            ThreadUtil.sleep(2000);

            lock.unlock();
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
            CacheLock lock = LockUtil.createCacheLock(key);
            String name = Thread.currentThread().getName();
            System.out.println(StrUtil.format("{}: 尝试上锁", name));
            try {
                // 等待其他线程，只有所有线程同时到达这里之后才会继续往下走
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            lock.lock();
            System.out.println();
            System.out.println(StrUtil.format("{}: 上锁成功", name));
            lock.lock();
            System.out.println(StrUtil.format("{}: 重入上锁成功", name));

            ThreadUtil.sleep(2000);

            lock.unlock();
            System.out.println(StrUtil.format("{}: 释放锁1", name));
            lock.unlock();
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


    @Test
    public void test_ss() {

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, com.developcollect.core.thread.ThreadUtil.createThreadFactoryBuilder().setDaemon(true).setNamePrefix("ORDER_SCAN_").build());
        executor.scheduleAtFixedRate(() -> {
            changePos(2349948568892340L, () -> {
                log.info("模拟订单状态更新");
                ThreadUtil.sleep(RandomUtil.randomLong(600, 3200));
            });
        }, 1, 1, TimeUnit.SECONDS);

        while (true) {
            changePos(2349948568892340L, () -> {
                log.info("模拟下单操作");
                ThreadUtil.sleep(RandomUtil.randomLong(300, 2600));
            });
            ThreadUtil.sleep(RandomUtil.randomLong(800, 1400));
        }
    }


    private void changePos(long id, Runnable runnable) {
        String lockKey = "STRATEGY_TRADE_LOCK_KEY_PREFIX" + id;
        CacheLock lock = LockUtil.createCacheLock(lockKey);
        log.debug("[TL] 尝试锁定[{}]  TH:[{}]", id, Thread.currentThread().getName());
        lock.lock();
        log.debug("[TL] 获取锁定[{}]  TH:[{}]", id, Thread.currentThread().getName());
        try {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("Run Error: ", e);
                throw e;
            }
        } finally {
            log.debug("[TL] 尝试释放[{}]  TH:[{}]", id, Thread.currentThread().getName());
            lock.unlock();
            log.debug("[TL] 释放锁定[{}]  TH:[{}]", id, Thread.currentThread().getName());
        }
    }
}