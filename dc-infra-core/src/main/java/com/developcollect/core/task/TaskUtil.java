package com.developcollect.core.task;

import com.developcollect.core.thread.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/26 15:47
 */
public class TaskUtil {
    private static Logger log = LoggerFactory.getLogger(TaskUtil.class);
    private static TaskUtil util = new TaskUtil();
    private volatile boolean isStop = false;
    private Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private AtomicLong idGenerator = new AtomicLong();

    private TaskUtil() {
        startThread();
    }

    public static long addTask(Task task) {
        if (util.tasks.containsValue(task)) {
            throw new IllegalArgumentException("任务已存在");
        }
        util.tasks.put(task.id(), task);
        return task.id();
    }

    public static void replaceTask(Task task) {
        if (util.tasks.containsKey(task.id())) {
            util.tasks.put(task.id(), task);
        }
    }

    private void startThread() {
        Thread thread = ThreadUtil.newThread(() -> {
            while (!isStop) {
                if (!ThreadUtil.safeSleep(10)) {
                    log.error("休眠中断");
                    break;
                }

                long time = System.currentTimeMillis();

                for (Map.Entry<Long, Task> entry : tasks.entrySet()) {
                    Task task = entry.getValue();
                    if (time >= task.nextTriggerTime()) {
                        if (task.remainingTimes() > 0) {
                            if (task.reduceAndGetRemainingTimes() <= 0) {
                                tasks.remove(entry.getKey());
                            }
                            ThreadUtil.execAsync(() -> {
                                try {
                                    task.execute();
                                } catch (Exception ignore) {

                                }
                            });
                        }
                    }
                }

            }
        }, "TaskUtil-thread");
        thread.setDaemon(true);
        thread.start();
    }

    public static boolean containsTask(Task task) {
        return util.tasks.containsValue(task);
    }


    public static DelayTask newDelayTask(Runnable runnable, long delay) {
        return new DelayTask(util.idGenerator.incrementAndGet(), runnable, delay);
    }

    public static TimesTask newTimesTask(long times, Runnable runnable, Function<Long, Long> nextTriggerTimeFunc) {
        return new TimesTask(util.idGenerator.incrementAndGet(), times, runnable, nextTriggerTimeFunc);
    }
}
