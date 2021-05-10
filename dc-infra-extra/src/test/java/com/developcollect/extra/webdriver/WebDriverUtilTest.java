package com.developcollect.extra.webdriver;

import org.junit.Test;

import java.io.File;


public class WebDriverUtilTest {


    @Test
    public void test2() {
        String[] urls = new String[] {
                "https://baidu.com",
                "https://map.baidu.com/",
                "https://lbsyun.baidu.com/index.php?title=webapi",
                "https://music.163.com/",
                "https://36kr.com/"
        };

        for (String url : urls) {
            System.out.println("截图：" + url);
            File file = WebDriverUtil.screenshotFull(url, new FileOutputType("E:\\laboratory\\tmp"));
            System.out.println(file);
        }
    }

}