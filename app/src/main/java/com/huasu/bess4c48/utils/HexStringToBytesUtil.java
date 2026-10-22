package com.huasu.bess4c48.utils;

import android.util.Log;

public class HexStringToBytesUtil {
    private static final String TAG = "Hex2Bytes";

    /**
     * 将空格分隔的十六进制字符串转为字节数组
     * @param hexStr 格式示例："AA 55 80 12 00 01"
     * @return 对应的字节数组 | null（参数非法时）
     */
    public static byte[] hexStringToBytes(String hexStr) {
        // 1. 参数合法性校验
        if (hexStr == null || hexStr.trim().isEmpty()) {
            Log.e(TAG, "错误：十六进制字符串为空");
            return null;
        }

        // 2. 按空格分割为单个十六进制字符（处理多个连续空格的情况）
        String[] hexParts = hexStr.trim().split("\\s+");
        byte[] bytes = new byte[hexParts.length];

        // 3. 逐个转换为字节
        try {
            for (int i = 0; i < hexParts.length; i++) {
                String hexPart = hexParts[i].trim();
                // 校验单个十六进制字符长度（必须是2位）
                if (hexPart.length() != 2) {
                    Log.e(TAG, "错误：非法的十六进制字符：" + hexPart + "（位置：" + i + "）");
                    return null;
                }
                // 十六进制转十进制整数（radix=16），再强制转为byte
                int intValue = Integer.parseInt(hexPart, 16);
                bytes[i] = (byte) intValue;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "错误：十六进制字符转换失败：" + e.getMessage());
            return null;
        }

        return bytes;
    }

    // 辅助方法：字节数组转回十六进制字符串（用于验证转换结果）
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) return "";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // 转为两位大写十六进制，补零
            sb.append(String.format("%02X ", b));
        }
        // 去除最后一个空格
        return sb.toString().trim();
    }

    // 测试示例（验证你的目标字符串转换）
    public static void testConversion() {
        // 你的目标十六进制字符串
        String targetHexStr = "AA 55 80 12 00 01 02 00 90 62 00 20 99 41 00 00 5D 43 01 00 15 10 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 63 01 00 00 00 00 00 00 00 00 00 51 4B 01 00 B5 94 01 00 AB 41 00 00 0D 63 01 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 F5 9B 01 00 05 9C 01 00 15 9C 01 00 25 9C 01 00 AB 41 00 00 AB 41 00 00 19 2E 01 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 E5 2D 01 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 AB 41 00 00 8D FA 00 00 71 A2 00 00 00 F0 02 F8 00 F0 3C F8 0C A0 30 C8 08 38 24 18 2D 18 A2 46 67 1E AB 46 54 46 5D 46 AC 42 01 D1 00 F0 2E F8 7E 46 0F 3E 0F CC B6 46 01 26 33 42 00 D0 FB 1A A2 46 AB 46 33 43 18 47 9C 25 03 00 BC 25 03 00 00 23 00 24 00 25 00 26 10 3A 01";

        // 转换为字节数组
        byte[] resultBytes = hexStringToBytes(targetHexStr);

        // 验证结果（转回十六进制字符串，对比原字符串）
        if (resultBytes != null) {
            String verifyStr = bytesToHexString(resultBytes);
            Log.d(TAG, "转换成功，字节数组长度：" + resultBytes.length);
            Log.d(TAG, "验证：转回十六进制字符串=" + verifyStr);
            // 对比原字符串和转回的字符串是否一致（忽略空格格式）
            boolean isSame = targetHexStr.replace(" ", "").equals(verifyStr.replace(" ", ""));
            Log.d(TAG, "转换结果是否一致：" + isSame); // 应输出true
        } else {
            Log.e(TAG, "转换失败");
        }
    }
}
