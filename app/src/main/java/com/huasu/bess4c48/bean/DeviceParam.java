package com.huasu.bess4c48.bean;

import android.text.TextUtils;

import com.huasu.bess4c48.utils.CPResourceUtil;
import com.huasu.bess4c48.utils.InstructionUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class DeviceParam implements Serializable {
    //寄存器开始地址高字节
    public byte highAddr;
    //寄存器开始地址低字节
    public byte lowAddr;
    //读取到的数据
    public String readHexVal;
    //根据readHexVal转换后的数据
    public String convertVal;
    public String minVal;//参数最小值
    public String maxVal;//参数最大值
    public String paramUnit;//参数单位
    public String zoomStr = "";  //缩放
    public int offsetVal = 0; //偏移量
    public int precision = 0;//转换后的数据的保留小数点位
    //参数名
    public String paramName;
    public String writeValue = "";//新写入的值
    public int setting = 1;//是否可以设置  1,设置  0，不可设置
    public int position;
    public String resStr;
    public int itemType = 1;//普通的item 1；选择框2
    public boolean checked;
    public int imgResId;

    /**
     * 将读取到的寄存器的数据转换为具体的数值
     *
     * @return
     */
    public String convertHexToVal() {
        if (TextUtils.isEmpty(readHexVal))
            return "--";
        try {
            float zoom = TextUtils.isEmpty(zoomStr) ? 1 : Float.parseFloat(zoomStr);
            int decimal = Integer.parseInt(readHexVal, 16);
            decimal = decimal - offsetVal;
            float after = decimal * zoom;
            String formattedString = String.format("%." + precision + "f", after);
            return formattedString;
        } catch (Exception e) {
            e.printStackTrace();
            return "--";
        }
    }


    /**
     * 将写入的值转换成两个字节的数组
     *
     * @return
     */
    public byte[] convertToByte() {
        BigDecimal zoom = TextUtils.isEmpty(zoomStr) ? new BigDecimal("1") : new BigDecimal(zoomStr);
        BigDecimal data = new BigDecimal(writeValue).divide(zoom);
        data = data.add(new BigDecimal(offsetVal));
        if (data.compareTo(BigDecimal.ZERO) >= 0 && (data.scale() <= 0 || data.stripTrailingZeros().scale() <= 0)) {
            List<String> cmdList = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(data.intValue()), 2);
            return new byte[]{InstructionUtil.hexToByte(cmdList.get(0)), InstructionUtil.hexToByte(cmdList.get(1))};
        }
        return null;
    }

    /**
     * 将写入的值转换成int
     *
     * @return
     */
    public int convertToInt() {
        BigDecimal data = new BigDecimal(writeValue).divide(new BigDecimal(zoomStr));
        data = data.add(new BigDecimal(offsetVal));
        if (data.compareTo(BigDecimal.ZERO) >= 0 && (data.scale() <= 0 || data.stripTrailingZeros().scale() <= 0)) {
            return data.intValue();
        }
        return 0;
    }

    public String getConvertVal() {
        if (TextUtils.isEmpty(convertVal))
            return "--";
        if (TextUtils.isEmpty(resStr))
            return convertVal;
        return CPResourceUtil.getResStringById(resStr + convertVal);
    }

    public boolean isSetting() {
        return setting == 1;
    }

    public String getRangeNew() {
        if (TextUtils.isEmpty(minVal) || TextUtils.isEmpty(maxVal)) {
            return "--";
        }
        return minVal + " ~ " + maxVal + (TextUtils.isEmpty(paramUnit) ? "" : paramUnit);
    }
}
