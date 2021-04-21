package com.developcollect.core.task;

import cn.hutool.core.lang.Assert;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/26 15:59
 */
public class TimesTask implements Task {

    protected AtomicLong remainingTimes;
    protected AtomicLong alreadyRunTimes = new AtomicLong(1);
    protected Runnable runnable;
    protected Function<Long, Long> nextTriggerTimeFunc;
    private long id;

    TimesTask(long id, long times, Runnable runnable, Function<Long, Long> nextTriggerTimeFunc) {
        Assert.isTrue(times > 0);
        Assert.notNull(runnable);
        Assert.notNull(nextTriggerTimeFunc);
        this.remainingTimes = new AtomicLong(times);
        this.nextTriggerTimeFunc = nextTriggerTimeFunc;
        this.runnable = runnable;
        this.id = id;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public long remainingTimes() {
        return remainingTimes.get();
    }

    @Override
    public long reduceAndGetRemainingTimes() {
        return remainingTimes.decrementAndGet();
    }

    @Override
    public void execute() {
        try {
            runnable.run();
        } catch (Exception ignore) {
        }

        alreadyRunTimes.incrementAndGet();
    }

    @Override
    public long nextTriggerTime() {
        return nextTriggerTimeFunc.apply(alreadyRunTimes.get());
    }


}
