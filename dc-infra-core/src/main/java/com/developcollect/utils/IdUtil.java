package com.developcollect.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Singleton;
import com.developcollect.lang.Sequence;

import java.time.LocalDateTime;

/**
 * @author zak
 * @version 1.0
 * @date 2020/9/30 16:22
 */
public class IdUtil extends cn.hutool.core.util.IdUtil {

    /**
     * 创建一个Sequence算法的id生成器
     *
     * @param workerId
     * @param datacenterId
     * @author zak
     * @date 2020/9/30 16:37
     */
    public static Sequence createSequence(long workerId, long datacenterId) {
        return new Sequence(workerId, datacenterId);
    }

    /**
     * 获取一个单例的Sequence算法的id生成器
     *
     * @param workerId
     * @param datacenterId
     * @author zak
     * @date 2020/9/30 16:37
     */
    public static Sequence getSequence(long workerId, long datacenterId) {
        return Singleton.get(Sequence.class, workerId, datacenterId);
    }

    /**
     * 以主机和进程的机器码获取一个单例的Sequence算法的id生成器
     */
    public static Sequence getSequence() {
        return Singleton.get(Sequence.class);
    }

    /**
     * 获取唯一ID
     *
     * @return id
     */
    public static long getId() {
        return getSequence().nextId();
    }


    /**
     * 获取唯一ID
     *
     * @return id
     */
    public static String getIdStr() {
        return Long.toBinaryString(getId());
    }


    /**
     * 格式化的毫秒时间
     */
    public static String getMillisecond() {
        return DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmssSSS");
    }

    /**
     * 时间 ID = Time + ID
     * <p>例如：可用于商品订单 ID</p>
     */
    public static String getTimeId() {
        return getMillisecond() + getIdStr();
    }


    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 简化的UUID，去掉了横线
     * @since 4.1.19
     */
    public static String get32UUID() {
        return fastSimpleUUID();
    }


    /**
     * 缩短SequenceId
     *
     * @param id id
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/11/7 9:36
     */
    public static String shortSequenceId(long id) {
        String shortId = MathUtil.convertRadix(Long.toString(id - 1200000000000000000L), 62);
        return shortId;
    }

    /**
     * 还原被缩短的SequenceId
     *
     * @param shortSequenceId 被缩短后的SequenceId
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/11/7 9:36
     */
    public static long stretchShortSequenceId(String shortSequenceId) {
        return Long.parseLong(MathUtil.convertRadix(shortSequenceId, 62, 10)) + 1200000000000000000L;
    }
}
