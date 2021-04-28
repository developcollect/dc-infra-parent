package com.developcollect.extra.javacv;

import com.developcollect.core.utils.LambdaUtil;
import com.developcollect.core.utils.StrUtil;
import com.developcollect.extra.javacv.meta.AudioMetaInfo;
import com.developcollect.extra.javacv.meta.VideoMetaInfo;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/4/28 13:43
 */
public class MediaUtil {


    public static AudioMetaInfo getAudioMetaInfo(String audioFilePath) {
        return grab(audioFilePath, MediaUtil::getAudioMetaInfo);
    }

    public static AudioMetaInfo getAudioMetaInfo(InputStream inputStream) {
        return grab(inputStream, MediaUtil::getAudioMetaInfo);
    }

    public static VideoMetaInfo getVideoMetaInfo(String videoFilePath)  {
        return grab(videoFilePath, MediaUtil::getVideoMetaInfo);
    }

    public static VideoMetaInfo getVideoMetaInfo(InputStream inputStream)  {
        return grab(inputStream, MediaUtil::getVideoMetaInfo);
    }


    private static <T> T grab(String mediaPath, FFmpegGrabFunction<T> metaInfoFetcher) {
        FFmpegFrameGrabber grabber = null;
        try {
            grabber = FFmpegFrameGrabber.createDefault(mediaPath);
            grabber.start();
            return metaInfoFetcher.grab(grabber);
        } catch (FFmpegFrameGrabber.Exception e) {
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

    private static <T> T grab(InputStream inputStream, FFmpegGrabFunction<T> metaInfoFetcher) {
        FFmpegFrameGrabber grabber = null;
        try {
            grabber = new FFmpegFrameGrabber(inputStream);
            grabber.start();
            return metaInfoFetcher.grab(grabber);
        } catch (FFmpegFrameGrabber.Exception e) {
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

    private static AudioMetaInfo getAudioMetaInfo(FFmpegFrameGrabber grabber) {
        AudioMetaInfo audioMetaInfo = new AudioMetaInfo();
        audioMetaInfo.setBitRate(grabber.getAudioBitrate());
        audioMetaInfo.setDuration(grabber.getLengthInTime());
        audioMetaInfo.setSampleRate(grabber.getSampleRate());
        audioMetaInfo.setChannels(grabber.getAudioChannels());
        audioMetaInfo.setMetadata(grabber.getAudioMetadata());
        audioMetaInfo.setSize(audioMetaInfo.getBitRate() * audioMetaInfo.getDuration() / 1000000 / 8);
        try (BytePointer name = avcodec.avcodec_find_decoder(grabber.getAudioCodec()).name()) {
            audioMetaInfo.setCodec(name.getString());
        }
        return audioMetaInfo;
    }



    /**
     * 截取视频中的一帧
     * @param videoFilePath 视频文件路径
     * @param n 第几帧
     * @return 图片
     */
    public static BufferedImage getScreenshot(String videoFilePath, int n) {
        return grab(videoFilePath, grabber -> getScreenshot(grabber, n, true));
    }

    public static BufferedImage getScreenshot(InputStream inputStream, int n) {
        return grab(inputStream, grabber -> getScreenshot(grabber, n, true));
    }


    private static BufferedImage getScreenshot(FFmpegFrameGrabber grabber, int n, boolean isFrame) throws FFmpegFrameGrabber.Exception {
        if (n < 0) {
            return null;
        }
        // 视频总帧数
        int frames = grabber.getLengthInAudioFrames();
        int targetFrame = n;
        // 在指定的秒数
        if (!isFrame) {
            double videoFrameRate = grabber.getVideoFrameRate();
            targetFrame = (int) (videoFrameRate * n);
        }
        Frame frame;
        for (int i = 0, count = 0; i < frames; i++) {
            // 获取视频帧
            frame = grabber.grabImage();
            if (frame == null || frame.image == null) {
                continue;
            }
            count++;
            if (count == targetFrame) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bi = converter.getBufferedImage(frame);
                return bi;
            }
        }
        return null;
    }


    public static void getAudioFromVideo() {
        // todo 从视频中抽取出音频
    }

    // todo 压缩视频




//    /**
//     * @Description: 获取视频截图
//     * @throws IOException  void
//     */
//    public static Map<String, Object> getScreenshot(String filePath) throws Exception{
//
//        System.out.println("截取视频截图开始："+ System.currentTimeMillis());
//        Map<String, Object> result = new HashMap<String, Object>();
//        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(filePath);
//
//        // 第一帧图片存储位置
//        String targerFilePath = filePath.substring(0, filePath.lastIndexOf("\\"));
//        // 视频文件名
//        String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
//        // 图片名称
//        String targetFileName = fileName.substring(0, fileName.lastIndexOf("."));
//        System.out.println("视频路径是：" + targerFilePath);
//        System.out.println("视频文件名：" + fileName);
//        System.out.println("图片名称是：" + targetFileName);
//
//        grabber.start();
//        //设置视频截取帧（默认取第一帧）
//        Frame frame = grabber.grabImage();
//        //视频旋转度
//        String rotate = grabber.getVideoMetadata("rotate");
//        Java2DFrameConverter converter = new Java2DFrameConverter();
//        //绘制图片
//        BufferedImage bi = converter.getBufferedImage(frame);
//        if (rotate != null) {
//            // 旋转图片
//            bi = rotate(bi, Integer.parseInt(rotate));
//        }
//        //图片的类型
//        String imageMat = "jpg";
//        //图片的完整路径
//        String imagePath = targerFilePath + File.separator + targetFileName + "." + imageMat;
//        //创建文件
//        File output = new File(imagePath);
//        ImageIO.write(bi, imageMat, output);
//
//        //拼接Map信息
//        result.put("videoWide", bi.getWidth());
//        result.put("videoHigh", bi.getHeight());
//        long duration = grabber.getLengthInTime() / (1000 * 1000);
//        result.put("rotate", StrUtil.isBlank(rotate)? "0" : rotate);
//        result.put("format", grabber.getFormat());
//        result.put("imgPath", output.getPath());
//        System.out.println("视频的宽:" + bi.getWidth());
//        System.out.println("视频的高:" + bi.getHeight());
//        System.out.println("视频的旋转度：" + rotate);
//        System.out.println("视频的格式：" + grabber.getFormat());
//        System.out.println("此视频时长（s/秒）：" + duration);
//        grabber.stop();
//        System.out.println("截取视频截图结束："+ System.currentTimeMillis());
//        return result;
//    }







//
//    /**
//     * @Description: 根据视频旋转度来调整图片
//     * @param src
//     * @param angel	视频旋转度
//     * @return  BufferedImage
//     */
//    public static BufferedImage rotate(BufferedImage src, int angel) {
//        int src_width = src.getWidth(null);
//        int src_height = src.getHeight(null);
//        int type = src.getColorModel().getTransparency();
//        Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);
//        BufferedImage bi = new BufferedImage(rect_des.width, rect_des.height, type);
//        Graphics2D g2 = bi.createGraphics();
//        g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
//        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
//        g2.drawImage(src, 0, 0, null);
//        g2.dispose();
//        return bi;
//    }
//
//
//    /**
//     * @Description: 计算图片旋转大小
//     * @param src
//     * @param angel
//     * @return  Rectangle
//     */
//    public static Rectangle calcRotatedSize(Rectangle src, int angel) {
//        if (angel >= 90) {
//            if (angel / 90 % 2 == 1) {
//                int temp = src.height;
//                src.height = src.width;
//                src.width = temp;
//            }
//            angel = angel % 90;
//        }
//        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
//        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
//        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
//        double angel_dalta_width = Math.atan((double) src.height / src.width);
//        double angel_dalta_height = Math.atan((double) src.width / src.height);
//        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
//        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
//        int des_width = src.width + len_dalta_width * 2;
//        int des_height = src.height + len_dalta_height * 2;
//        return new java.awt.Rectangle(new Dimension(des_width, des_height));
//    }
}
