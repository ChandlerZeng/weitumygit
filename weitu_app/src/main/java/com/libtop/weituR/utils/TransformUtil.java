package com.libtop.weituR.utils;

import java.math.BigDecimal;

/**
 * Created by LianTu on 2016/4/25.
 */
public class TransformUtil {

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节),省略小数点后的数字
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return ((int)returnValue + "M");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return ((int)returnValue + "K");
    }
}
