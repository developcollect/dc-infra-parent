package com.developcollect.core.cli;


import lombok.Setter;

/**
 * 使用ANSI终端指令码\033[K清除光标后面的字符。
 *
 * 使用\033[F使用光标回到上一行,这里要注意，光标回到上一行后下面的一行会被清除。
 */
public class CliProgressBar {


    @Setter
    private long total = 100;

    /**
     * 用于进度条显示的字符
     */
    @Setter
    private char showChar = '▧';
    @Setter
    private char emptyChar = ' ';

    @Setter
    private String title = "进度";


    private int barLen = 100;

    public CliProgressBar() {
    }

    /**
     * 使用系统标准输出，显示字符进度条及其百分比
     */
    public CliProgressBar(int barLen, char showChar) {
        this.total = barLen;
        this.showChar = showChar;
    }

    /**
     * 显示进度条
     */
    public void show(long value) {
        if (value < 0) {
            return;
        }
        if (value > total) {
            value = total;
        }

        draw(value);
        if (value == total) {
            afterComplete();
        }
    }

    /**
     * 画指定长度个showChar
     */
    private void draw(long value ) {
        double rate = calcRate(value, total);

        int len = (int) (rate * barLen);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < barLen; i++) {
            if (i < len) {
                sb.append(showChar);
            } else {
                sb.append(emptyChar);
            }
        }
        System.out.printf("\r %s: %s | %2.2f%%", title, sb, rate*100);
    }

    private static double calcRate(long value, long total) {
        double rate;
        if (value == total) {
            rate = 1;
        } else {
            rate = ((double) value / total);
        }
        return rate;
    }

    /**
     * 完成后换行
     */
    private void afterComplete() {
        System.out.print('\n');
    }





}
