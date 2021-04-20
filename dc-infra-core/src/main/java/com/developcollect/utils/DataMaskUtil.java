package com.developcollect.utils;


import cn.hutool.core.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据脱敏工具类
 *
 * @author zak
 * @since 1.0.0
 */
public class DataMaskUtil {

    private static final int PHONE_LENGTH = 11;

    private static final Pattern TEL_PATTERN = Pattern.compile("\\(?\\d{2,3}[)\\-_](\\d{7,8})");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^([A-Za-z0-9\\u4e00-\\u9fa5]+)(@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)$");

    /**
     * 对手机号进行脱敏
     * 13707096483  -> 137****6483
     *
     * @param phone
     * @return java.lang.String
     * @author zak
     * @date 2019/12/26 9:50
     */
    public static String dePhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return phone;
        }
        if (phone.length() != PHONE_LENGTH) {
            return phone;
        }
        final char[] chars = phone.toCharArray();
        chars[3] = chars[4] = chars[5] = chars[6] = '*';
        return String.valueOf(chars);
    }


    /**
     * 对座机号进行脱敏
     * (010)32764748 -> (010)32***748
     * 020-88888888  -> 020-88***888
     *
     * @param tel
     * @return java.lang.String
     * @author zak
     * @date 2019/12/26 10:01
     */
    public static String deTel(String tel) {
        if (StrUtil.isBlank(tel)) {
            return tel;
        }

        final Matcher matcher = TEL_PATTERN.matcher(tel);
        if (matcher.find()) {
            final String num = matcher.group(1);
            char[] chars = num.toCharArray();
            if (chars.length == 7) {
                char[] newChars = new char[8];
                System.arraycopy(chars, 0, newChars, 0, 2);
                System.arraycopy(chars, 4, newChars, 5, 3);
                chars = newChars;
            }
            chars[2] = chars[3] = chars[4] = '*';
            return matcher.group().replaceFirst(num, String.valueOf(chars));
        }
        return tel;
    }


    public static String deEmail(String email) {
        if (StrUtil.isBlank(email)) {
            return email;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (matcher.find()) {
            String g1 = matcher.group(1);
            String g2 = matcher.group(2);
            String dg1 = g1.substring(0, Math.min(g1.length(), 2));
            return dg1 + "**" + g2;
        }
        return email;
    }
}
