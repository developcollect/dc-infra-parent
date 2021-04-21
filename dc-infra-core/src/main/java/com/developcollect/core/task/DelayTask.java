package com.developcollect.core.task;


import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 延迟执行任务
 *
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/26 16:29
 */
public class DelayTask extends TimesTask {

    private long bornTime;
    private long delay;
    private AtomicBoolean fired;

    DelayTask(long id, Runnable runnable, long delay) {
        super(id, 1, runnable, c -> 1L);
        this.bornTime = System.currentTimeMillis();
        this.delay = delay;
        this.fired = new AtomicBoolean(false);
    }


    @Override
    public long nextTriggerTime() {
        return bornTime + delay;
    }

    @Override
    public void execute() {
        super.execute();
        fired.set(true);
    }

    public boolean fired() {
        return fired.get();
    }


    public void reset() {
        reset(this.delay);
    }

    public void reset(long delay) {
        this.remainingTimes.set(1);
        this.bornTime = System.currentTimeMillis();
        this.delay = delay;
    }
}
