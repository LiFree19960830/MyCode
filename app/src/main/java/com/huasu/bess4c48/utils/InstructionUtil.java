package com.huasu.bess4c48.utils;

import android.util.Log;

import com.huasu.bess4c48.bean.SelectDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * TODO 串口指令工具类
 * Created by zhangfangyuan on 2019/12/17.
 */
public class InstructionUtil {
    /**
     * TODO 组装JNI指令
     *
     * @param value TODO 指令字节数组
     * @return
     */

    public static byte[] packageInstruction(byte[] value) {

        String crcResult = getCRC2(value);

        Log.i(InstructionUtil.class.getSimpleName(), "----------crcResult为:" + crcResult + "----------");

        List<String> cmdList = getDivLines(crcResult, 2);

        Log.i(InstructionUtil.class.getSimpleName(), "----------cmdList为:" + cmdList.get(0) + "," + cmdList.get(1) + "----------");
        Log.i(InstructionUtil.class.getSimpleName(), "----------DataUtils.HexToByte为:" + hexToByte(cmdList.get(0)) + "," + hexToByte(cmdList.get(1)) + "----------");

        byte[] E1 = new byte[]{hexToByte(cmdList.get(0)), hexToByte(cmdList.get(1))};
        Log.i(InstructionUtil.class.getSimpleName(), "----------E1为:" + Arrays.toString(E1) + "----------");
        byte[] cmdByte = unitByteArray(value, E1);
        Log.i(InstructionUtil.class.getSimpleName(), "----------cmdByte为:" + Arrays.toString(cmdByte) + "----------");
        return cmdByte;
    }

    public static byte[] packageInstruction(byte[] value, boolean reverse) {

        String crcResult = getCRC2(value);

//        Log.i(InstructionUtil.class.getSimpleName(), "----------crcResult为:" + crcResult + "----------");

        List<String> cmdList = getDivLines(crcResult, 2);

//        Log.i(InstructionUtil.class.getSimpleName(), "----------cmdList为:" + cmdList.get(0) + "," + cmdList.get(1) + "----------");
//        Log.i(InstructionUtil.class.getSimpleName(), "----------DataUtils.HexToByte为:" + hexToByte(cmdList.get(0)) + "," + hexToByte(cmdList.get(1)) + "----------");

        byte[] E1;
        if (reverse)
            E1 = new byte[]{hexToByte(cmdList.get(1)), hexToByte(cmdList.get(0))};
        else
            E1 = new byte[]{hexToByte(cmdList.get(0)), hexToByte(cmdList.get(1))};
//        Log.i(InstructionUtil.class.getSimpleName(), "----------E1为:" + Arrays.toString(E1) + "----------");
        byte[] cmdByte = unitByteArray(value, E1);
//        Log.i(InstructionUtil.class.getSimpleName(), "----------cmdByte为:" + Arrays.toString(cmdByte) + "----------");
        return cmdByte;
    }

    /**
     * 按照指定长度切割字符串
     *
     * @param inputString 需要切割的源字符串
     * @param length      指定的长度
     * @return
     */
    public static List<String> getDivLines(String inputString, int length) {
        List<String> divList = new ArrayList<>();
        int remainder = (inputString.length()) % length;
        // 一共要分割成几段
        int number = (int) Math.floor((inputString.length()) / length);
        for (int index = 0; index < number; index++) {
            String childStr = inputString.substring(index * length, (index + 1) * length);
            divList.add(childStr);
        }
        if (remainder > 0) {
            String cStr = inputString.substring(number * length, inputString.length());
            divList.add(cStr);
        }
        return divList;
    }


    public static String getCRC2(byte[] bytes) {
//        ModBus 通信协议的 CRC ( 冗余循环校验码含2个字节, 即 16 位二进制数。
//        CRC 码由发送设备计算, 放置于所发送信息帧的尾部。
//        接收信息设备再重新计算所接收信息 (除 CRC 之外的部分）的 CRC,
//        比较计算得到的 CRC 是否与接收到CRC相符, 如果两者不相符, 则认为数据出错。
//
//        1) 预置 1 个 16 位的寄存器为十六进制FFFF(即全为 1) , 称此寄存器为 CRC寄存器。
//        2) 把第一个 8 位二进制数据 (通信信息帧的第一个字节) 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器。
//        3) 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位。
//        4) 如果移出位为 0, 重复第 3 步 ( 再次右移一位); 如果移出位为 1, CRC 寄存器与多项式A001 ( 1010 0000 0000 0001) 进行异或。
//        5) 重复步骤 3 和步骤 4, 直到右移 8 次,这样整个8位数据全部进行了处理。
//        6) 重复步骤 2 到步骤 5, 进行通信信息帧下一个字节的处理。
//        7) 将该通信信息帧所有字节按上述步骤计算完成后,得到的16位CRC寄存器的高、低字节进行交换。
//        8) 最后得到的 CRC寄存器内容即为 CRC码。

        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //高低位转换，看情况使用（譬如本人这次对led彩屏的通讯开发就规定校验码高位在前低位在后，也就不需要转换高低位)
        CRC = ((CRC & 0x0000FF00) >> 8) | ((CRC & 0x000000FF) << 8);
        String s = Integer.toHexString(CRC);
        if (s.length() == 3) return "0" + s;
        if (s.length() == 2) return "00" + s;
        if (s.length() == 1) return "000" + s;
        return s;
    }

    //-------------------------------------------------------
    //Hex字符串转byte
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
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


    public static byte[] numberTo4Bytes(long number, boolean isBigEndian) {
        byte[] bytes = new byte[4]; // 固定4个字节
        if (isBigEndian) {
            // 大端序：高位在前（字节0=最高位，字节3=最低位）
            bytes[0] = (byte) (number >> 24); // 取第31-24位
            bytes[1] = (byte) (number >> 16); // 取第23-16位
            bytes[2] = (byte) (number >> 8);  // 取第15-8位
            bytes[3] = (byte) number;         // 取第7-0位
        } else {
            // 小端序：低位在前（字节0=最低位，字节3=最高位）
            bytes[0] = (byte) number;         // 取第7-0位
            bytes[1] = (byte) (number >> 8);  // 取第15-8位
            bytes[2] = (byte) (number >> 16); // 取第23-16位
            bytes[3] = (byte) (number >> 24); // 取第31-24位
        }
        System.out.println(ByteUtils.bytesToHexString(bytes));
        return bytes;
    }

    /**
     * 将数字转换为2字节数组（指定字节序）
     * @param number 待转换数字（自动转为short，超出范围截断低16位）
     * @param isBigEndian 是否大端序（true=大端，false=小端）
     * @return 固定长度为2的字节数组
     */
    public static byte[] numberTo2Bytes(int number, boolean isBigEndian) {
        byte[] bytes = new byte[2]; // 固定2个字节
        // 先转为short（截断为16位）
        short shortValue = (short) number;

        if (isBigEndian) {
            // 大端序：高位在前（字节0=高8位，字节1=低8位）
            bytes[0] = (byte) (shortValue >> 8);  // 取第15-8位（高8位）
            bytes[1] = (byte) shortValue;         // 取第7-0位（低8位）
        } else {
            // 小端序：低位在前（字节0=低8位，字节1=高8位）
            bytes[0] = (byte) shortValue;         // 取第7-0位（低8位）
            bytes[1] = (byte) (shortValue >> 8);  // 取第15-8位（高8位）
        }
        return bytes;
    }

    public static byte[] numberTo1Byte(int number) {
        byte[] byteArr = new byte[1];
        // 强制转换为byte（自动截断为低8位）
        byteArr[0] = (byte) number;
        return byteArr;
    }

    public static String byte2Hex(byte val) {
        return String.format("%02X", val);
    }

    public static void main(String[] args) {
        SelectDate selectDate = new SelectDate();
        selectDate.year=2025;
        selectDate.month = 10;
        selectDate.day=20;
        selectDate.hour=5;
        selectDate.minute=1;
        selectDate.second=1;


      int[] aa = {
                Integer.parseInt(selectDate.year - 2000 + String.format(Locale.getDefault(), "%02d", selectDate.month)),
                Integer.parseInt(String.format(Locale.getDefault(), "%02d", selectDate.day) +  String.format(Locale.getDefault(), "%02d", selectDate.hour)),
              Integer.parseInt(String.format(Locale.getDefault(), "%02d", selectDate.minute) +  String.format(Locale.getDefault(), "%02d", selectDate.second))};
        for (int i = 0; i < aa.length; i ++) {
            // 截取4位分段字符串

            // 🔴 步骤3：十进制转十六进制（核心API）
            // Integer.parseInt(fourBitStr) → 十进制转整数
            // Integer.toHexString() → 整数转16进制小写；toUpperCase() 转大写
            String hex = Integer.toHexString(aa[i]).toUpperCase();

            System.out.println("\n" + aa[i] + " → " + hex);
        }

    }

    public static String intTo2Hex(int intHex) {
//        return String.format("%04s", new Object[]{Integer.toHexString(intHex)});
        String s = Integer.toHexString(intHex).toUpperCase();
        if (s.length() == 1) {
            return "000" + s;
        } else if (s.length() == 2) {
            return "00" + s;
        }
        if (s.length() == 3) {
            return "0" + s;
        } else if (s.length() == 4) {
            return s;
        }
        return null;
    }

}
