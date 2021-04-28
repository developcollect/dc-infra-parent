package com.developcollect.extra.javacv.meta;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/28 14:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VideoMetaInfo extends MetaInfo {

    /**
     * 视频（帧）宽度 ，单位为px
     */
    private int width;
    /**
     * 视频（帧）高度 ，单位为px
     */
    private int height;
    /**
     * 视频时长 ,单位：毫秒
     */
    private long duration;
    /**
     * 比特率，单位：Kb/s
     * 指视频每秒传送（包含）的比特数
     */
    private int bitRate;
    /**
     * 编码器
     */
    private String codec;
    /**
     * 帧率，单位：FPS（Frame Per Second）
     * 指视频每秒包含的帧数
     */
    private double frameRate;

    /**
     * 视频旋转角度
     **/
    private int rotate;

    /**
     * 帧数
     */
    private int frames;

    /**
     * 视频中包含的音频信息
     */
    private AudioMetaInfo audioMetaInfo;

}
