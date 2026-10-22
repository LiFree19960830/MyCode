package com.huasu.bess4c48.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ByteUtils {

    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //-------------------------------------------------------
    //1字节转2个Hex字符
    public static String byte2Hex(Byte inByte) {
        return String.format("%02x", new Object[]{inByte}).toUpperCase();
    }

    /**
     * TODO 合并byte数组,拼接不完整的数据包
     */
    public static byte[] unitByteArray(byte[] byte1, byte[] byte2) {
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }

    public static String hexStr2Str(String hexString) {
        // 用于接收转换结果
        String result = "";
        // 转大写
        hexString = hexString.toUpperCase();
        // 16进制字符
        String hexDigital = "0123456789ABCDEF";
        // 将16进制字符串转换成char数组
        char[] hexs = hexString.toCharArray();
        // 能被16整除，肯定可以被2整除
        byte[] bytes = new byte[hexString.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = hexDigital.indexOf(hexs[2 * i]) * 16 + hexDigital.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        // byte[]-->String
        try {
            result = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制转化十进制，带符号
     *
     * @param
     * @return
     */
    public static int hex2ToTempera(String s) {
        int parseInt = Integer.parseInt(s, 16);
        if (parseInt < 32768) return parseInt;
        return parseInt - 65536;
    }


    public static String byteArrTo2Hex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte valueOf : inBytArr) {
            strBuilder.append(byte2Hex(Byte.valueOf(valueOf)));
            strBuilder.append("");
        }
        return strBuilder.toString();
    }

    /**
     * 把byteArray转为Date
     * //11 03 06 13 0C 17 08 21 0A E2 36
     * //13 0C 17 08 21 0A----> 19-12-23 08:33:10  2019-12-23 08:33:10
     *
     * @param byteArray 长度为6
     * @return
     */
    public static Long byteArrayToDate(byte[] byteArray) {
        Date date = null;
        String dateStr = "20" + String.format("%02d", hex2ToTempera(byte2Hex(byteArray[0]))) + "-" +
                String.format("%02d", hex2ToTempera(byte2Hex(byteArray[1]))) + "-" +
                String.format("%02d", hex2ToTempera(byte2Hex(byteArray[2]))) + " " +
                String.format("%02d", hex2ToTempera(byte2Hex(byteArray[3]))) + ":" +
                String.format("%02d", hex2ToTempera(byte2Hex(byteArray[4]))) + ":" +
                String.format("%02d", hex2ToTempera(byte2Hex(byteArray[5])));
        try {
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            Log.i("CommonUtil--", "error:" + e.getMessage());
        }
        return date.getTime();
    }

    /**
     * TODO 截取byte数组   不改变原数组
     *
     * @param b      原数组
     * @param off    偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public static byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    public static byte[] intTo2Byte(int data) {
        byte[] byteArray = new byte[2];
        byteArray[0] = (byte) data;
        byteArray[1] = (byte) (data >> 8);
        return byteArray;
    }

    public static void main(String[] args) {
        String aa = "01";
        System.out.println(aa.getBytes()[0]);
    }
}
