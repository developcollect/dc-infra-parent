package com.developcollect.core.thread.lock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MultiLock implements Lock {

    private List<Lock> locks = new ArrayList<>();

    @Override
    public void lock() {
        throw new UnsupportedOperationException("MultiLock`s lock method is unsupported");
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("MultiLock`s lock method is unsupported");
    }

    @Override
    public boolean tryLock() {
        RuntimeException ex = null;
        boolean allLocked = true;
        List<Lock> lockedLocks = new LinkedList<>();
        for (Lock lock : locks) {
            try {
                if (lock.tryLock()) {
                    lockedLocks.add(lock);
                } else {
                    allLocked = false;
                    break;
                }
            } catch (RuntimeException e) {
                allLocked = false;
                ex = e;
            }
        }

        if (!allLocked) {
            try {
                unlockAll(lockedLocks);
            } catch (Exception ignore) {
            }
        }

        if (ex != null) {
            throw ex;
        }

        return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        RuntimeException ex = null;
        long deadline = System.nanoTime() + unit.toNanos(time);
        boolean allLocked = true;
        List<Lock> lockedLocks = new LinkedList<>();
        for (Lock lock : locks) {
            try {
                if (lock.tryLock(deadline - System.nanoTime(), TimeUnit.NANOSECONDS)) {
                    lockedLocks.add(lock);
                } else {
                    allLocked = false;
                    break;
                }
            } catch (RuntimeException e) {
                allLocked = false;
                ex = e;
            }
        }

        if (!allLocked) {
            try {
                unlockAll(lockedLocks);
            } catch (Exception ignore) {
            }
        }

        if (ex != null) {
            throw ex;
        }

        return true;
    }

    @Override
    public void unlock() {
        unlockAll(locks);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("MultiLock`s newCondition method is unsupported");
    }

    private void unlockAll(List<Lock> locks) {
        RuntimeException ex = null;
        for (Lock lock : locks) {
            try {
                lock.unlock();
            } catch (RuntimeException e) {
                ex = e;
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
}
