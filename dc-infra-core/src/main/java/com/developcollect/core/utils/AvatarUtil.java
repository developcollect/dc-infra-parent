package com.developcollect.core.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Random;


/**
 * 头像工具类
 * @author zak
 * @deprecated 使用单独的头像生成项目 dc-avatar
 */
@Deprecated
public class AvatarUtil {

    /**
     * 可以直接在<img/>标签或者浏览器地址栏预览的base64编码头
     */
    private static final String BASE64_PREFIX = "data:image/png;base64,";

    /**
     * 生成头像的base64编码，并且拼接html中img标签中src属性需要的头
     * 可直接放到img标签使用
     * <br>
     *     例如：<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAYAAAA5ZDbSAAABEklEQVR42u3dMQ0AMAhFwRrtgv8BB62NH7jhCYBbSTjd/TS3YwmABViABViABViAAWsdcN1SUIABAwYMGLAAC7AACzBgwIABAxZgARZgAQYMGDBgwIABC3DCwNvmBQwYMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAEDBgwYMGABBgwYMGDAgAELsAALsAADBgwYMGABFmABFmDAgAEDBmyp04HTSz/i830UMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAEDBgwYMGDAgAEDBgx4J7AAAxZgARZgARZgARbg4X3NFS7VSpcJjAAAAABJRU5ErkJggg=="/>
     * @param id id
     */
    public static String createHtmlSrc(int id) {
        return BASE64_PREFIX + createBase64(id);
    }

    /**
     * 生成头像的base64编码
     * @param id id
     */
    public static String createBase64(int id)  {
        return Base64.encode(createBytes(id));
    }

    /**
     * 根据id生成一个头像，颜色随机。
     * @param id id
     */
    public static byte[] createBytes(int id)  {
        return ImgUtil.toBytes(createImg(id), "png");
    }

    /**
     * 根据id生成一个头像，颜色随机。
     * @param id id
     */
    public static BufferedImage createImg(int id)  {
        int width = 20;
        int grid = 5;
        int padding = width / 2;
        int size = width * grid + width;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D _2d = img.createGraphics();
        _2d.setColor(new Color(240, 240, 240));
        _2d.fillRect(0, 0, size, size);
        _2d.setColor(randomColor(80, 200));
        char[] idchars = createIdent(id);
        int i = idchars.length;
        for (int x = 0; x < Math.ceil(grid / 2.0); x++) {
            for (int y = 0; y < grid; y++) {
                if (idchars[--i] < 53) {
                    _2d.fillRect((padding + x * width), (padding + y * width), width, width);
                    if (x < grid / 2.0) {
                        _2d.fillRect((padding + ((grid - 1) - x) * width), (padding + y * width), width, width);
                    }
                }
            }
        }

        _2d.dispose();

        return img;
    }

    private static Color randomColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(Math.abs(bc - fc));
        int g = fc + random.nextInt(Math.abs(bc - fc));
        int b = fc + random.nextInt(Math.abs(bc - fc));
        return new Color(r, g, b);
    }

    private static char[] createIdent(int id) {
        BigInteger bi_content = new BigInteger(String.valueOf(id).getBytes());
        int absId = Math.abs(id);
        BigInteger bi = new BigInteger(absId + "identicon" + absId, 36);
        bi = bi.xor(bi_content);
        return bi.toString(10).toCharArray();
    }
}