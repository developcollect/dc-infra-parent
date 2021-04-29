package com.developcollect.extra.javacv;

import com.developcollect.core.utils.LambdaUtil;
import com.developcollect.core.utils.StrUtil;
import com.developcollect.extra.javacv.meta.AudioMetaInfo;
import com.developcollect.extra.javacv.meta.VideoMetaInfo;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/28 13:43
 */
public class MediaUtil {


    /**
     * 获取音频信息
     * @param audioFilePath 音频文件路径
     * @return 音频信息
     */
    public static AudioMetaInfo getAudioMetaInfo(String audioFilePath) {
        return grab(audioFilePath, MediaUtil::getAudioMetaInfo);
    }

    /**
     * 获取音频信息
     * @param inputStream 输入流
     * @return 音频信息
     */
    public static AudioMetaInfo getAudioMetaInfo(InputStream inputStream) {
        return grab(inputStream, MediaUtil::getAudioMetaInfo);
    }

    /**
     * 获取视频信息
     * @param videoFilePath 视频文件路径
     * @return 视频信息
     */
    public static VideoMetaInfo getVideoMetaInfo(String videoFilePath)  {
        return grab(videoFilePath, MediaUtil::getVideoMetaInfo);
    }

    /**
     * 获取视频信息
     * @param inputStream 输入流
     * @return 视频信息
     */
    public static VideoMetaInfo getVideoMetaInfo(InputStream inputStream)  {
        return grab(inputStream, MediaUtil::getVideoMetaInfo);
    }


    /**
     * 处理多媒体文件，在这个方法中会开启FFmpegFrameGrabber和关闭FFmpegFrameGrabber
     * @param mediaPath 媒体文件路径
     * @param grabFunction 处理方法
     */
    private static <T> T grab(String mediaPath, FFmpegGrabFunction<T> grabFunction) {
        FFmpegFrameGrabber grabber = null;
        try {
            grabber = FFmpegFrameGrabber.createDefault(mediaPath);
            grabber.start();
            return grabFunction.grab(grabber);
        } catch (Exception e) {
            return LambdaUtil.raise(e);
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                } catch (FFmpegFrameGrabber.Exception e) {
                    LambdaUtil.raise(e);
                }
            }
        }
    }

    /**
     * 处理多媒体文件，在这个方法中会开启FFmpegFrameGrabber和关闭FFmpegFrameGrabber
     * @param inputStream 媒体文件输入流
     * @param grabFunction 处理方法
     */
    private static <T> T grab(InputStream inputStream, FFmpegGrabFunction<T> grabFunction) {
        FFmpegFrameGrabber grabber = null;
        try {
            grabber = new FFmpegFrameGrabber(inputStream);
            grabber.start();
            return grabFunction.grab(grabber);
        } catch (Exception e) {
            return LambdaUtil.raise(e);
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                } catch (FFmpegFrameGrabber.Exception e) {
                    LambdaUtil.raise(e);
                }
            }
        }
    }

    /**
     * 获取视频信息
     * @param grabber FFmpegFrameGrabber
     */
    private static VideoMetaInfo getVideoMetaInfo(FFmpegFrameGrabber grabber) {
        VideoMetaInfo videoMetaInfo = new VideoMetaInfo();
        // 时长
        videoMetaInfo.setDuration(grabber.getLengthInTime());
        // 帧率
        videoMetaInfo.setFrameRate(grabber.getVideoFrameRate());
        // 视频帧数
        videoMetaInfo.setFrames(grabber.getLengthInVideoFrames());
        // 比特率
        videoMetaInfo.setBitRate(grabber.getVideoBitrate());
        // 格式
        videoMetaInfo.setFormat(grabber.getFormat());
        // 视频宽度
        videoMetaInfo.setWidth(grabber.getImageWidth());
        // 视频高度
        videoMetaInfo.setHeight(grabber.getImageHeight());
        // 编码器
        try (BytePointer name = avcodec.avcodec_find_decoder(grabber.getVideoCodec()).name()) {
            videoMetaInfo.setCodec(name.getString());
        }

        // 视频旋转度
        try {
            String rotate = grabber.getVideoMetadata("rotate");
            if (StrUtil.isBlank(rotate)) {
                videoMetaInfo.setRotate(0);
            } else {
                videoMetaInfo.setRotate(Integer.parseInt(rotate));
            }
        } catch (NumberFormatException e) {
            videoMetaInfo.setRotate(0);
        }

        // 视频中的音频信息
        if (grabber.hasAudio()) {
            videoMetaInfo.setAudioMetaInfo(getAudioMetaInfo(grabber));
        }


        // file_size = video_size + audio_size;
        // file_size = (video_bitrate + audio_bitrate) * time_in_seconds / 8;
        long bitRate = videoMetaInfo.getBitRate();
        if (videoMetaInfo.getAudioMetaInfo() != null) {
            bitRate += videoMetaInfo.getAudioMetaInfo().getBitRate();
        }
        videoMetaInfo.setSize(bitRate * videoMetaInfo.getDuration() / 1000000 / 8);
        videoMetaInfo.setMetadata(grabber.getVideoMetadata());
        return videoMetaInfo;
    }

    /**
     * 获取音频信息
     * @param grabber FFmpegFrameGrabber
     */
    private static AudioMetaInfo getAudioMetaInfo(FFmpegFrameGrabber grabber) {
        AudioMetaInfo audioMetaInfo = new AudioMetaInfo();
        audioMetaInfo.setBitRate(grabber.getAudioBitrate());
        audioMetaInfo.setDuration(grabber.getLengthInTime());
        audioMetaInfo.setSampleRate(grabber.getSampleRate());
        audioMetaInfo.setChannels(grabber.getAudioChannels());
        audioMetaInfo.setMetadata(grabber.getAudioMetadata());
        audioMetaInfo.setSize(audioMetaInfo.getBitRate() * audioMetaInfo.getDuration() / 1000000 / 8);
        audioMetaInfo.setFormat(grabber.getFormat());
        try (BytePointer name = avcodec.avcodec_find_decoder(grabber.getAudioCodec()).name()) {
            audioMetaInfo.setCodec(name.getString());
        }
        return audioMetaInfo;
    }

    /**
     * 截取视频中的指定帧
     * @param videoFilePath 视频文件路径
     * @param select 第几帧
     * @return 图片
     */
    public static BufferedImage getScreenshotInFrame(String videoFilePath, int select) {
        return getScreenshotInFrame(videoFilePath, select, true);
    }

    public static BufferedImage getScreenshotInFrame(String videoFilePath, int select, boolean checkOver) {
        return grab(videoFilePath, grabber -> getScreenshot(grabber, select, true, checkOver));
    }

    public static BufferedImage getScreenshotInFrame(InputStream inputStream, int select) {
        return getScreenshotInFrame(inputStream, select, true);
    }

    public static BufferedImage getScreenshotInFrame(InputStream inputStream, int select, boolean checkOver) {
        return grab(inputStream, grabber -> getScreenshot(grabber, select, true, true));
    }

    /**
     * 截取视频中的指定时间的一帧
     * @param videoFilePath 视频文件路径
     * @param select 第几秒
     * @return 图片
     */
    public static BufferedImage getScreenshotInSecond(String videoFilePath, int select) {
        return getScreenshotInSecond(videoFilePath, select, true);
    }

    public static BufferedImage getScreenshotInSecond(String videoFilePath, int select, boolean checkOver) {
        return grab(videoFilePath, grabber -> getScreenshot(grabber, select, false, checkOver));
    }


    public static BufferedImage getScreenshotInSecond(InputStream inputStream, int select) {
        return getScreenshotInSecond(inputStream, select, true);
    }

    public static BufferedImage getScreenshotInSecond(InputStream inputStream, int select, boolean checkOver) {
        return grab(inputStream, grabber -> getScreenshot(grabber, select, true, checkOver));
    }

    /**
     * 获取视频封面
     * @param videoFilePath
     * @return
     */
    public static BufferedImage getVideoPoster(String videoFilePath) {
        return getScreenshotInSecond(videoFilePath, 10, false);
    }

    public static BufferedImage getVideoPoster(InputStream inputStream) {
        return getScreenshotInSecond(inputStream, 10, false);
    }


    /**
     * 截取视频中的一帧
     * @param grabber FFmpegFrameGrabber
     * @param select 截取位置
     * @param isFrame 截取位置是指指定帧还是指定秒
     * @param checkOver 是否检验截取位置超过视频最大位置，如果检测则在超过时返回null，如果不检测则返回最大位置的一帧
     * @return BufferedImage
     */
    private static BufferedImage getScreenshot(FFmpegFrameGrabber grabber, int select, boolean isFrame, boolean checkOver) throws FFmpegFrameGrabber.Exception {
        if (select < 0) {
            return null;
        }
        // 视频总帧数
        int frames = grabber.getLengthInAudioFrames();
        int targetFrame = select;
        // 在指定的秒数
        if (!isFrame) {
            double videoFrameRate = grabber.getVideoFrameRate();
            targetFrame = (int) (videoFrameRate * select);
        }
        if (checkOver && targetFrame > frames) {
            return null;
        }

        Frame frame = null;
        for (int i = 0, count = 0; i < frames; i++) {
            // 获取视频帧
            frame = grabber.grabImage();
            if (frame == null || frame.image == null) {
                continue;
            }
            count++;
            if (count == targetFrame) {
                break;
            }
        }

        if (frame != null && frame.image != null) {
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bi = converter.getBufferedImage(frame);
            return bi;
        }
        return null;
    }


    /**
     * 从视频中抽取出音频
     * @param videoFilePath 视频文件路径
     * @param targetFilePath 音频文件保存路径
     */
    public static void getAudioFromVideo(String videoFilePath, String targetFilePath) {
        grab(videoFilePath, grabber -> {
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(targetFilePath, grabber.getAudioChannels());
            getAudioFromVideo(grabber, recorder);
            return null;
        });
    }

    public static void getAudioFromVideo(String videoFilePath, OutputStream outputStream) {
        grab(videoFilePath, grabber -> {
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputStream, grabber.getAudioChannels());
            getAudioFromVideo(grabber, recorder);
            return null;
        });
    }

    public static void getAudioFromVideo(InputStream videoInputStream, String targetFilePath) {
        grab(videoInputStream, grabber -> {
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(targetFilePath, grabber.getAudioChannels());
            getAudioFromVideo(grabber, recorder);
            return null;
        });
    }

    public static void getAudioFromVideo(InputStream videoInputStream, OutputStream outputStream) {
        grab(videoInputStream, grabber -> {
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputStream, grabber.getAudioChannels());
            getAudioFromVideo(grabber, recorder);
            return null;
        });
    }

    private static void getAudioFromVideo(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder) throws FFmpegFrameRecorder.Exception, FFmpegFrameGrabber.Exception {
        recorder.setFormat("mp3");
        recorder.setSampleRate(grabber.getSampleRate());
        recorder.setTimestamp(grabber.getTimestamp());
        recorder.setAudioQuality(0);
        recorder.start();
        Frame frame;

        while (true){
            frame = grabber.grab();
            if (frame == null) {
                break;
            }
            if (frame.samples != null) {
                recorder.recordSamples(frame.sampleRate, frame.audioChannels, frame.samples);
            }
        }
        recorder.stop();
        recorder.release();
        try {
            recorder.close();
        } catch (FrameRecorder.Exception e) {
        }
    }

    // todo 压缩视频


}
