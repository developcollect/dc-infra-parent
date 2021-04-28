package com.developcollect.extra.javacv.meta;

import lombok.Data;
import lombok.ToString;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/28 14:55
 */
@Data
@ToString(callSuper = true)
public class AudioMetaInfo extends MetaInfo {

    /**
     * 音频时长 ,单位：毫秒
     */
    private long duration;
    /**
     * 比特率，单位：Kb/s
     * 指音频每秒传送（包含）的比特数
     */
    private int bitRate;

    /**
     * 采样频率，单位：Hz
     * 指一秒钟内对声音信号的采样次数
     */
    private int sampleRate;

    /**
     * 声道
     */
    private int channels;

    private String codec;
}
