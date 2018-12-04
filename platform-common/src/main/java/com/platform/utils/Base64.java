package com.platform.utils;

/**
 * 作者: @author Harmon <br>
 * 时间: 2017-08-11 16:17<br>
 * 描述: Base64 <br>
 */
public class Base64 {
    // 加密
    public static String encode(String str) {
        byte[] bytes = org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes());
        return bytes.toString();
//        byte[] b = null;
//        String s = null;
//        try {
//            b = str.getBytes("utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        if (b != null) {
//            s = new BASE64Encoder().encode(b);
//        }
//        return s;
    }

    // 解密
    public static String decode(String s) {
        byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(s);
        return bytes.toString();
        /*byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;*/
    }
    // 解密
    public static byte[] decodeToBytes(String s) {
        byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(s);
        return bytes;
        /*byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;*/
    }
}