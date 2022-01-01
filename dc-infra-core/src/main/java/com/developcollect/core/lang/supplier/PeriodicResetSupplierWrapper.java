package com.developcollect.core.lang.supplier;

import com.developcollect.core.lang.SystemClock;
import com.developcollect.core.utils.DateUtil;

import java.util.List;


/**
 * 周期重置
 *
 * @param <T>
 */
public class PeriodicResetSupplierWrapper<T> implements ResettableSupplier<T> {

    private final ResettableSupplier<T> delegate;
    private final long beginTime;
    private long prevTime;
    private final long period;

    public PeriodicResetSupplierWrapper(ResettableSupplier<T> supplier, long period) {
        this(supplier, DateUtil.beginOfPeriod(SystemClock.now(), period), period);
    }

    public PeriodicResetSupplierWrapper(ResettableSupplier<T> supplier, long beginTime, long period) {
        this.beginTime = beginTime;
        this.prevTime = beginTime;
        this.period = period;
        this.delegate = supplier;
    }

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
        long diff = SystemClock.now() - prevTime;
        if (diff > period) {
            long lastNodeNow = diff % period;
            prevTime += diff - lastNodeNow;
            reset();
        }
        return delegate.get();
    }
}
