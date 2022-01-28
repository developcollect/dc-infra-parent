package com.developcollect.core.schedule;

import com.developcollect.core.utils.ReflectUtil;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduleRegistry {

    @Getter
    protected ScheduledExecutorService scheduledExecutorService;

    public ScheduleRegistry(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void register(Object obj) {
        Method[] methods = ReflectUtil.getMethods(obj.getClass());
        for (Method method : methods) {
            if (!method.isSynthetic()) {
                Scheduled anno = method.getAnnotation(Scheduled.class);
                if (anno != null) {
                    if (method.getParameterCount() > 0) {
                        throw new IllegalArgumentException(method + "方法不能有参数");
                    }

                    long fixedDelay = anno.fixedDelay();
                    long fixedRate = anno.fixedRate();
                    long initialDelay = anno.initialDelay();
                    Runnable command = () -> ReflectUtil.invoke(obj, method);

                    if (fixedDelay > -1) {
                        scheduledExecutorService.scheduleWithFixedDelay(command, initialDelay, fixedDelay, anno.timeUnit());
                    } else if (fixedRate > -1) {
                        scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, fixedRate, anno.timeUnit());
                    } else {
                        scheduledExecutorService.schedule(command, initialDelay, anno.timeUnit());
                    }
                }
            }
        }
    }


}
