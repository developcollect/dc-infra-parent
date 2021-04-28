package com.developcollect.extra.javacv;

import com.developcollect.core.utils.FileUtil;
import com.developcollect.extra.javacv.meta.VideoMetaInfo;
import org.junit.Test;

import java.io.BufferedInputStream;

public class MediaUtilTeste {



    @Test
    public void test() {

        VideoMetaInfo videoMetaInfo = MediaUtil.getVideoMetaInfo("C:\\Users\\win005\\Videos\\Concession_LAN_800k.mp4");
        System.out.println(videoMetaInfo);
        System.out.println(FileUtil.humanSize(videoMetaInfo.getSize()));
        System.out.println(FileUtil.humanSize(videoMetaInfo.getAudioMetaInfo().getSize()));
//        MediaUtil.setFFmpegPath("D:\\Program-g\\ffmpeg-20190519-fbdb3aa-win64-static\\bin\\ffmpeg.exe");
//        com.developcollect.extra.media.VideoMetaInfo videoMetaInfo1 = MediaUtil.getVideoMetaInfo(new File("C:\\Users\\win005\\Videos\\Concession_LAN_800k.mp4"));
//        System.out.println(videoMetaInfo1);
//        System.out.println(1);
    }

    @Test
    public void test2() {
        BufferedInputStream inputStream = FileUtil.getInputStream("C:\\Users\\win005\\Videos\\Concession_LAN_800k.mp4");
        VideoMetaInfo videoMetaInfo = MediaUtil.getVideoMetaInfo(inputStream);
        System.out.println(videoMetaInfo);
        System.out.println(FileUtil.humanSize(videoMetaInfo.getSize()));
        System.out.println(FileUtil.humanSize(videoMetaInfo.getAudioMetaInfo().getSize()));
    }
}