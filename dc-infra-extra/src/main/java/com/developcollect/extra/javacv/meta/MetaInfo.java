package com.developcollect.extra.javacv.meta;

import lombok.Data;

import java.util.Map;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/28 14:55
 */
@Data
public class MetaInfo {
    /**
     * 多媒体的大小，指的是存储体积，单位为Byte
     * 注意：这个大小可能和文件实际占用大小用误差，这个大小时根据比特率和时长计算出来的
     *   file_size = video_size + audio_size;
     *   file_size = (video_bitrate + audio_bitrate) * time_in_seconds / 8;
     */
    private Long size;

    /**
     * 格式
     */
    private String format;

    private Map<String, String> metadata;
}
