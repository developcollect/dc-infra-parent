package com.developcollect.extra.javacv;

import org.bytedeco.javacv.FFmpegFrameGrabber;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/28 17:04
 */
@FunctionalInterface
public interface FFmpegGrabFunction<T> {

    T grab(FFmpegFrameGrabber grabber) throws Exception;

}
