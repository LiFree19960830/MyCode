package com.huasu.bess4c48.utils;

import android.util.ArrayMap;

import java.util.Map;

public class BleConstant {

    /**
     * 描述 UUID
     */
    public static String DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    /**
     * OTA服务 UUID
     */
    public static String OTA_SERVICE_UUID = "0000fee7-0000-1000-8000-00805f9b34fb";
    /**
     * OTA特征（特性）写入 UUID
     */
    public static String OTA_CHARACTERISTIC_WRITE_UUID = "0000fec7-0000-1000-8000-00805f9b34fb";

    /**
     * OTA特征（特性）读取 UUID
     */
    public static String OTA_CHARACTERISTIC_READ_UUID = "0000fec8-0000-1000-8000-00805f9b34fb";

    /**
     * OTA特征（特性）表示/读取 UUID
     */
    public static String OTA_CHARACTERISTIC_INDICATE_UUID = "0000fec8-0000-1000-8000-00805f9b34fb";

    public static Map<String, Byte> addressCode = new ArrayMap<>();

    static {
        addressCode.put("00", (byte) 0x00);
        addressCode.put("01", (byte) 0x01);
        addressCode.put("02", (byte) 0x02);
        addressCode.put("03", (byte) 0x03);
        addressCode.put("04", (byte) 0x04);
        addressCode.put("05", (byte) 0x05);
        addressCode.put("06", (byte) 0x06);
        addressCode.put("07", (byte) 0x07);
        addressCode.put("08", (byte) 0x08);
        addressCode.put("09", (byte) 0x09);
        addressCode.put("10", (byte) 0x0A);
        addressCode.put("11", (byte) 0x0B);
        addressCode.put("12", (byte) 0x0C);
        addressCode.put("13", (byte) 0x0D);
        addressCode.put("14", (byte) 0x0E);
        addressCode.put("15", (byte) 0x0F);
        addressCode.put("16", (byte) 0x10);
    }


}
