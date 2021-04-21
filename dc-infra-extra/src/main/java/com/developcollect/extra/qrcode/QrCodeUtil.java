package com.developcollect.extra.qrcode;


import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.qrcode.BufferedImageLuminanceSource;
import cn.hutool.extra.qrcode.QrCodeException;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/26 13:39
 */
public class QrCodeUtil extends cn.hutool.extra.qrcode.QrCodeUtil {

    private static final Set<BarcodeFormat> FORMAT_1D_SET;

    static {
        Set<BarcodeFormat> tmp = new HashSet<>();
        tmp.add(BarcodeFormat.CODABAR);
        tmp.add(BarcodeFormat.CODE_39);
        tmp.add(BarcodeFormat.CODE_93);
        tmp.add(BarcodeFormat.CODE_128);
        tmp.add(BarcodeFormat.EAN_8);
        tmp.add(BarcodeFormat.EAN_13);
        tmp.add(BarcodeFormat.ITF);
        tmp.add(BarcodeFormat.RSS_14);
        tmp.add(BarcodeFormat.RSS_EXPANDED);
        tmp.add(BarcodeFormat.UPC_A);
        tmp.add(BarcodeFormat.UPC_E);
        tmp.add(BarcodeFormat.UPC_EAN_EXTENSION);
        FORMAT_1D_SET = Collections.unmodifiableSet(tmp);
    }


    public static Result decodeResult(Image image, boolean isTryHarder, boolean isPureBarcode) {
        final MultiFormatReader formatReader = new MultiFormatReader();

        final LuminanceSource source = new BufferedImageLuminanceSource(ImgUtil.toBufferedImage(image));
        final Binarizer binarizer = new HybridBinarizer(source);
        final BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);

        final HashMap<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, CharsetUtil.UTF_8);
        // 优化精度
        hints.put(DecodeHintType.TRY_HARDER, isTryHarder);
        // 复杂模式，开启PURE_BARCODE模式
        hints.put(DecodeHintType.PURE_BARCODE, isPureBarcode);
        Result result;
        try {
            result = formatReader.decode(binaryBitmap, hints);
        } catch (NotFoundException e) {
            // 报错尝试关闭复杂模式
            hints.remove(DecodeHintType.PURE_BARCODE);
            try {
                result = formatReader.decode(binaryBitmap, hints);
            } catch (NotFoundException e1) {
                throw new QrCodeException(e1);
            }
        }

        return result;
    }


    /**
     * 是否一维码
     */
    public static boolean is1dBarcode(BarcodeFormat format) {
        return FORMAT_1D_SET.contains(format);
    }

    public static boolean is2dBarcode(BarcodeFormat format) {
        return !is1dBarcode(format);
    }
}
