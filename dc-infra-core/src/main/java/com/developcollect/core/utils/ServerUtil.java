package com.developcollect.core.utils;

import com.developcollect.core.lang.Sequence;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;


@Slf4j
public class ServerUtil {

    private static volatile Long serverIdentity;
    private static final Object LOCK = new Object();

    /**
     * 根据当前的网卡信息和jvm进程号生成一个服务唯一标识
     *
     * @return 服务唯一标识
     */
    public static long getServerIdentity() {
        if (serverIdentity == null) {
            synchronized (LOCK) {
                if (serverIdentity == null) {
                    try {
                        long maxWorkerId = ~(-1L << 16);
                        long maxDatacenterId = ~(-1L << 16);

                        Method getDatacenterId = ReflectUtil.getMethod(Sequence.class, "getDatacenterId", long.class);
                        long datacenterId = ReflectUtil.invokeStatic(getDatacenterId, maxDatacenterId);

                        Method getMaxWorkerId = ReflectUtil.getMethod(Sequence.class, "getMaxWorkerId", long.class, long.class);
                        long workerId = ReflectUtil.invokeStatic(getMaxWorkerId, datacenterId, maxWorkerId);

                        serverIdentity = datacenterId << 16 | workerId;
                    } catch (Exception e) {
                        log.error("生成服务唯一标识异常", e);
                        throw e;
                    }
                }
            }
        }

        return serverIdentity;
    }


}
