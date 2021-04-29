package com.developcollect.extra.javacv;

import org.bytedeco.javacv.FFmpegFrameRecorder;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/29 13:03
 */
@FunctionalInterface
public interface FFmpegRecordFunction {

    void record(FFmpegFrameRecorder recorder) throws Exception;
}
