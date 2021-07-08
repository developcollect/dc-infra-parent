package com.developcollect.core.lang;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import com.developcollect.core.utils.StrUtil;

import java.util.function.Supplier;

public class Assert extends cn.hutool.core.lang.Assert {



    public static <X extends Throwable> void isMatch(String regex, CharSequence content, Supplier<? extends X> supplier) throws X {
        if (!ReUtil.isMatch(regex, content)) {
            throw supplier.get();
        }
    }

    public static <X extends Throwable> void isMatch(String regex, CharSequence content, String errorMsgTemplate, Object... params) throws X {
        isMatch(regex, content, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    public static <X extends Throwable> void isMatch(String regex, CharSequence content) throws X {
        isMatch(regex, content, "[Assertion failed] - this content must be match regex");
    }


    public static int[] notEmpty(int[] array) {
        return notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }

    public static int[] notEmpty(int[] array, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notEmpty(array, () -> new IllegalArgumentException(cn.hutool.core.util.StrUtil.format(errorMsgTemplate, params)));
    }

    public static <X extends Throwable> int[] notEmpty(int[] array, Supplier<X> errorSupplier) throws X {
        if (ArrayUtil.isEmpty(array)) {
            throw errorSupplier.get();
        }
        return array;
    }
}
