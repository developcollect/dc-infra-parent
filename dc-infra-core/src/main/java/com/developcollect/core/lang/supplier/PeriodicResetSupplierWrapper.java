package com.developcollect.core.lang.supplier;

import com.developcollect.core.lang.SystemClock;

import java.util.List;


/**
 * 周期重置
 *
 * @param <T>
 */
public class PeriodicResetSupplierWrapper<T> implements ResettableSupplier<T> {

    private ResettableSupplier<T> delegate;
    private long beginTime;
    private long prevTime;
    private long period;

    @Override
    public void reset() {
        delegate.reset();
    }

    @Override
    public List<T> elements() {
        return delegate.elements();
    }

    @Override
    public T get() {
        long now = SystemClock.now();
        if (now - prevTime > period) {
            reset();
        }
        return delegate.get();
    }
}
