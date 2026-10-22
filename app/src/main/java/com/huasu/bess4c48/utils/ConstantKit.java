package com.huasu.bess4c48.utils;

import android.util.ArrayMap;

import java.math.BigDecimal;
import java.util.Map;

public class ConstantKit {
    public static final String BATTERY_DATA_FOLDER = "c48";
    public static final String BATTERY_DATA_EXPORT_FOLDER = "export";//导出文件
    public static int TYPE_HIGH_VOLT_ALARM = 0;
    public static int TYPE_HIGH_VOLT_PROTECT = 0;
    public static int TYPE_LOW_VOLT_ALARM = 1;
    public static int TYPE_LOW_VOLT_PROTECT = 1;
    public static int TYPE_HIGH_TEMP_ALARM = 10086;
    public static int TYPE_HIGH_TEMP_PROTECT = 10086;
    public static int TYPE_LOW_TEMP_ALARM = 10087;
    public static int TYPE_LOW_TEMP_PROTECT = 10087;
    public static final BigDecimal monomerVoltageEnlargeMultiple = new BigDecimal("1000");
    public static final BigDecimal monomerTemperatureEnlargeMultiple = new BigDecimal("10");
    public static final BigDecimal monomerTemperatureOffset = new BigDecimal("-500");

    public static final BigDecimal groupVoltageEnlargeMultiple = new BigDecimal("100");

    public static final BigDecimal groupElectricCurrentEnlargeMultiple = new BigDecimal("100");
    public static final BigDecimal groupElectricCurrentOffset = new BigDecimal("-30000");

    public static final BigDecimal mosTempEnlargeMultiple = new BigDecimal("10");
    public static final BigDecimal mosTempOffset = new BigDecimal("-500");

    public static final BigDecimal temperature1EnlargeMultiple = new BigDecimal("10");
    public static final BigDecimal temperature1Offset = new BigDecimal("-500");

    public static final BigDecimal avgTempEnlargeMultiple = new BigDecimal("10");
    public static final BigDecimal avgTempOffset = new BigDecimal("-500");

    public static final BigDecimal positiveResistanceEnlargeMultiple = new BigDecimal("1000");
    public static final BigDecimal negativeResistanceEnlargeMultiple = new BigDecimal("1000");

    public static final BigDecimal limitPowerCurrentMultiple = new BigDecimal("10");
    public static final BigDecimal powerEnlargeMultiple = new BigDecimal("10");

    //TODO 2020-04-17 add
    public static final BigDecimal groupSOCLowestEnlargeMultiple = new BigDecimal("10");
    public static final BigDecimal groupSOHLowestEnlargeMultiple = new BigDecimal("10");
    public static final BigDecimal groupSOCGroupVoltageEnlargeMultiple = new BigDecimal("10");
    public static final BigDecimal groupSOHGroupVoltageEnlargeMultiple = new BigDecimal("10");
    public static final BigDecimal hydrogenContentEnlargeMultiple = new BigDecimal("10");
    public static final BigDecimal capacityEnlargeMultiple = new BigDecimal("100");

}
