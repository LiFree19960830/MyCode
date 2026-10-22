package com.huasu.bess4c48.bean;


import android.text.TextUtils;

import androidx.collection.ArrayMap;

import com.huasu.bess4c48.utils.ToolUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 电池组信息
 */
public class BTGRealtimeData {
    public Long reportTime;
    public int chargeStatus;//TODO 电池充放电状态（0：静置，1：放电，2：充电，3：未充未放，4：加热，5：浮充）
    public Short groupStatus;//TODO 电池组告警状态 (0：正常，1：告警，2：故障)，默认是0 正常
    public int batEqualizationStatus;//TODO 电池均衡状态（0：关，1：开）
    public int chgMos;//TODO 充电mos（0：关，1：开）
    public int disChgMos; //TODO 放电mos（0：关，1：开）
    public int heatingFilm; //TODO 加热膜（0：关，1：开）
    public int disChgCurrent; //TODO 充电限流（0：关，1：开）
    public int fullChgProtection; //TODO 满充保护（0：无，1：有）
    public int version;//程序字版本号

    public BigDecimal groupVoltage;
    public BigDecimal groupElectricCurrent;
    public BigDecimal envTemp;//环境温度1
    public BigDecimal mosTemp;//mos温度1
    public BigDecimal soc;
    public BigDecimal soh;
    public BigDecimal power;
    public BigDecimal avgVolt;//平均单体电压
    public BigDecimal avgTemp;//平均单体温度
    public BigDecimal maxVolt;//最高单体电压
    public int maxVoltNo;//最高单体电压号
    public BigDecimal minVolt;//最低单体电压
    public int minVoltNo;//最低单体电压号
    public BigDecimal rangeValue;//电池电压极差值
    public BigDecimal capacity;//电池容量
    public int disChgCycles;//放电循环次数
    public int chgCycles;//充电循环次数
    public BigDecimal btTemp5;//温度5
    public BigDecimal btTemp6;//温度6
    public BigDecimal btTemp7;//温度7
    public BigDecimal btTemp8;//温度8
    public int tempNum = 4;//温度个数
    public List<BTRealtimeData> btRealtimeDataList = new ArrayList<>();
    public List<Integer> alarms = new ArrayList<>(); //告警code集合
    public List<Integer> protects = new ArrayList<>(); //保护code集合

    public Map<Integer, List<Integer>> alarmsCellNoMap = new ArrayMap<>();  //告警电池号Map

    public Map<Integer, List<Integer>> protectsCellNoMap = new ArrayMap<>();  //保护电池号Map

    public String diff;

    /**
     * 返回格式化后的充电次数
     **/
    public String getChgCyclesString() {
        if (chgCycles == 65535 || chgCycles == 36727) {
            return String.valueOf(chgCycles);
        } else {
            int value = (int) Math.floor(chgCycles / 10);
            return String.valueOf(value);
        }
    }

    /**
     * 返回格式化后的放电次数
     **/
    public String getDisChgCyclesString() {
        if (disChgCycles == 65535 || disChgCycles == 36727) {
            return String.valueOf(disChgCycles);
        } else {
            int value = (int) Math.floor(disChgCycles / 10);
            return String.valueOf(value);
        }
    }

    public String getVolt() {
        if (groupVoltage == null)
            return "--";
        return String.valueOf(groupVoltage.setScale(2));
    }


    public String getCurrent() {
        if (groupElectricCurrent == null)
            return "--";
        return String.valueOf(groupElectricCurrent.setScale(2));
    }

    public String getMinVolt() {
        if (minVolt == null)
            return "--";
        return String.valueOf(minVolt);
    }

    public String getMaxVolt() {
        if (maxVolt == null)
            return "--";
        return String.valueOf(maxVolt);
    }

    public String getAvgVolt() {
        if (avgVolt == null)
            return "--";
        return String.valueOf(avgVolt);
    }

    public String getAvgTemp() {
        if (avgTemp == null)
            return "--";
        return String.valueOf(avgTemp);
    }

    public boolean isChg() {
        return chargeStatus == 2;
    }

    public boolean isDisChg() {
        return chargeStatus == 1;
    }

    public String getPower() {
        if (power == null)
            return "--";
        return String.valueOf(power);
    }

    public float getSOC() {
        if (soc == null)
            return 0;
        if (soc.compareTo(new BigDecimal("100")) == 0)
            return 100f;
        return soc.floatValue();
    }

    public String getSOCStr() {
        if (soc == null)
            return "0";
        if (soc.compareTo(new BigDecimal("100")) == 0)
            return "100";
        return String.valueOf(soc.floatValue());
    }

    public String getBtTemp5() {
        if (btTemp5 == null)
            return "--";
        return String.valueOf(btTemp5);
    }


    public String getBtTemp6() {
        if (btTemp6 == null)
            return "--";
        return String.valueOf(btTemp6);
    }


    public String getBtTemp7() {
        if (btTemp7 == null)
            return "--";
        return String.valueOf(btTemp7);
    }


    public String getBtTemp8() {
        if (btTemp8 == null)
            return "--";
        return String.valueOf(btTemp8);
    }
}
