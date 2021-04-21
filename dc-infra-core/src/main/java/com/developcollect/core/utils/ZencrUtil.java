package com.developcollect.core.utils;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * DIY的加密方法
 */
public class ZencrUtil {

    /**
     * 参数加密, 把value根据key加密
     *
     * @param key   key
     * @param value value
     * @return java.lang.String
     * @author zak
     * @date 2019/9/18 17:19
     **/
    public static String encrypt(String key, String value) {
        byte[] bytes = value.getBytes();
        int[] arr = new int[(int) Math.ceil(bytes.length / 4.0)];
        for (int i = 0, j = 0; i < bytes.length; i += 4, j++) {
            arr[j] = bytesToInt(bytes, i);
        }

        try {
            // 1. 对key sha1
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            byte[] md = mdTemp.digest(key.getBytes(StandardCharsets.UTF_8));

            // 2. 取结果的前32bit，然后跟要加密整数进行异或，得到一个加密后的32bit结果
            int ba = bytesToInt(md, 0);
            for (int i = 0; i < arr.length; i++) {
                arr[i] ^= ba;
            }

            // 3.对结果分组: 2bit | 6bit | 6bit | 6bit | 6bit | 6bit, 各个组分别取名为：a0、a1、a2、a3、a4、a5
            char[][] r = new char[arr.length][6];

            // 4. 将前面每个分组的值作为字典数组的下标，则加密结果为：dict[a0]dict[a1]dict[a2]dict[a3]dict[a4]dict[a5]
            for (int i = 0; i < r.length; i++) {
                char[] ca = r[i];
                int n = arr[i];

                ca[0] = convert((n & 0xC0000000) >>> 30);
                ca[1] = convert((n & 0x3f000000) >>> 24);
                ca[2] = convert((n & 0xFC0000) >>> 18);
                ca[3] = convert((n & 0x3F000) >>> 12);
                ca[4] = convert((n & 0xFC0) >>> 6);
                ca[5] = convert(n & 0x3F);
            }
            String str = Arrays.stream(r).map(String::valueOf).collect(Collectors.joining());
            return str;
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 参数解密, 根据key把value解密
     *
     * @param key   key
     * @param value value
     * @return java.lang.String
     * @author zak
     * @date 2019/9/18 17:19
     **/
    public static String decrypt(String key, String value) throws Exception {
        if (value.length() % 6 > 0) {
            throw new Exception("内容被篡改");
        }
        char[] chars = value.toCharArray();

        int[] arr = new int[6];
        byte[] content = new byte[value.length() / 6 * 4];
        for (int i = 0; i < chars.length; i += 6) {
            for (int j = 0; j < 6; j++) {
                char ch = chars[i + j];
                int n;
                if (ch >= '1' && ch <= '9') {
                    n = ch - 49;
                } else if (ch >= 'a' && ch <= 'z') {
                    n = ch - 97 + 9;
                } else if (ch >= 'A' && ch <= 'Z') {
                    n = ch - 65 + 9 + 26;
                } else if (ch == '-') {
                    n = 9 + 26 + 26;
                } else if (ch == '=') {
                    n = 9 + 26 + 26 + 1;
                } else if (ch == '*') {
                    n = 9 + 26 + 26 + 2;
                } else {
                    throw new Exception("内容被篡改");
                }
                arr[j] = n;
            }

            arr[0] = arr[0] << 30;
            arr[1] = arr[1] << 24;
            arr[2] = arr[2] << 18;
            arr[3] = arr[3] << 12;
            arr[4] = arr[4] << 6;

            int e = arr[0] | arr[1] | arr[2] | arr[3] | arr[4] | arr[5];
            try {
                MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
                byte[] md = mdTemp.digest(key.getBytes(StandardCharsets.UTF_8));
                int ba = bytesToInt(md, 0);
                e ^= ba;
                byte[] bytes = intToBytes(e);
                System.arraycopy(bytes, 0, content, i / 6 * 4, 4);
            } catch (Exception ignore) {
            }
        }

        int i;
        for (i = content.length; content.length > 0 && i >= 0; i--) {
            if (i != 0 && content[i - 1] != 0) {
                break;
            }
        }
        byte[] range = Arrays.copyOfRange(content, 0, i);
        return new String(range, StandardCharsets.UTF_8);
    }

    private static int bytesToInt(byte[] src, int offset) {
        byte b1 = offset >= src.length ? 0 : src[offset];
        byte b2 = offset + 1 >= src.length ? 0 : src[offset + 1];
        byte b3 = offset + 2 >= src.length ? 0 : src[offset + 2];
        byte b4 = offset + 3 >= src.length ? 0 : src[offset + 3];
        int value = (b1 & 0xFF)
                | ((b2 & 0xFF) << 8)
                | ((b3 & 0xFF) << 16)
                | ((b4 & 0xFF) << 24);
        return value;
    }

    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    private static char convert(int a) {
        if (a < 9) {
            return (char) (a + 49);
        } else if (a < 35) {
            return (char) (a + 88);
        } else if (a < 61) {
            return (char) (a + 30);
        } else if (a == 62) {
            return 61;
        } else if (a == 63) {
            return 42;
        } else if (a == 61) {
            return 45;
        }
        return 33;
    }

}
