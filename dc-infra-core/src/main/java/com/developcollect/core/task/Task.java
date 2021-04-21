package com.developcollect.core.task;

public interface Task extends cn.hutool.cron.task.Task {

    long id();


    /**
     * 剩余次数
     */
    default long remainingTimes() {
        return -1;
    }

    /**
     * 剩余次数减一，并获取新的剩余次数
     */
    default long reduceAndGetRemainingTimes() {
        return remainingTimes();
    }


    /**
     * 下一次触发时间
     */
    long nextTriggerTime();


}
