package com.fun.crawl.base.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {

    /**
     * 定义加密方式
     * MAC算法可选以下多种算法
     * <pre>
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    private static final String KEY_MAC = "HmacMD5";

    private static final String KEY_MAC_SHA1 = "HmacSHA1";

    private static final String CHARSET_UTF8 = "UTF-8";

    /*
        使用 HmacSha1 加密
     */
    public static String hmacSha1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] text = encryptText.getBytes(CHARSET_UTF8);
        byte[] keyData = encryptKey.getBytes(CHARSET_UTF8);

        SecretKeySpec secretKey = new SecretKeySpec(keyData, KEY_MAC_SHA1);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return byte2hex(mac.doFinal(text));
    }

    //二行制转字符串
    public static String byte2hex(byte[] b)
    {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

}
