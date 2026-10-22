package com.huasu.bess4c48.utils;

import com.huasu.bess4c48.bean.BTGRealtimeData;
import com.huasu.bess4c48.bean.BTRealtimeData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataUtils {

    /**
     * @Description:通过数组解析电池组数据,组装电池组实时信息
     * @Param: bytesn
     * @return: void
     * @Author: zhangfangyuan
     * @Date: 2019/12/18
     */
    public static void getBTGData(BTGRealtimeData btgRealtimeData, byte[] bytes) {
        if (btgRealtimeData == null)
            btgRealtimeData = new BTGRealtimeData();
        // TODO 数组不为空
        if (bytes != null && bytes.length > 0) {
            //TODO 电池组电压
            int groupVoltage = getNum(bytes[0], bytes[1]);
            System.out.println(groupVoltage);
            btgRealtimeData.groupVoltage = getPropertyScalingValues("groupVoltage", groupVoltage);

            //TODO 充放电电流
            int groupElectricCurrent = getNum(bytes[2], bytes[3]);
            System.out.println(groupElectricCurrent);
            btgRealtimeData.groupElectricCurrent = getPropertyScalingValues("groupElectricCurrent", groupElectricCurrent);

            //TODO 环境温度1
            int envTemp1 = getNum(bytes[4], bytes[5]);
            System.out.println(envTemp1);
            btgRealtimeData.envTemp = getPropertyScalingValues("envTemp", envTemp1);

            //TODO mos温度1
            int mosTemp = getNum(bytes[6], bytes[7]);
            System.out.println(mosTemp);
            btgRealtimeData.mosTemp = getPropertyScalingValues("mosTemp", mosTemp);

            //最高电压电池号
            int maxVoltNo = getNum(bytes[8], bytes[9]);
            btgRealtimeData.maxVoltNo = maxVoltNo;

            //最高电池电压
            int maxVolt = getNum(bytes[10], bytes[11]);
            btgRealtimeData.maxVolt = getPropertyScalingValues("monomerVoltage", maxVolt);

            //最低电压电池号
            int minVoltNo = getNum(bytes[12], bytes[13]);
            btgRealtimeData.minVoltNo = minVoltNo;

            //最低电池电压
            int minVolt = getNum(bytes[14], bytes[15]);
            btgRealtimeData.minVolt = getPropertyScalingValues("monomerVoltage", minVolt);

            //平均电池电压
            int avgVolt = getNum(bytes[16], bytes[17]);
            btgRealtimeData.avgVolt = getPropertyScalingValues("monomerVoltage", avgVolt);

            //电池电压极差值
            int rangeValue = getNum(bytes[20], bytes[21]);
            btgRealtimeData.rangeValue = getPropertyScalingValues("rangeValue", rangeValue);

            //电池平均温度
            int avgTemp = getNum(bytes[30], bytes[31]);
            btgRealtimeData.avgTemp = getPropertyScalingValues("avgTemp", avgTemp);

            //TODO soc
            int soc = getNum(bytes[32], bytes[33]);
            System.out.println(soc);
            btgRealtimeData.soc = getPropertyScalingValues("soc", soc);

            //TODO soh
            int soh = getNum(bytes[34], bytes[35]);
            System.out.println(soh);
            btgRealtimeData.soh = getPropertyScalingValues("soh", soh);


            //电芯温度5
            int btTemp5 = getNum(bytes[38], bytes[39]);
            btgRealtimeData.btTemp5 = getPropertyScalingValues("monomerTemperature", btTemp5);

            //电芯温度6
            int btTemp6 = getNum(bytes[40], bytes[41]);
            btgRealtimeData.btTemp6 = getPropertyScalingValues("monomerTemperature", btTemp6);

            //电芯温度7
            int btTemp7 = getNum(bytes[42], bytes[43]);
            btgRealtimeData.btTemp7 = getPropertyScalingValues("monomerTemperature", btTemp7);

            //电芯温度8
            int btTemp8 = getNum(bytes[44], bytes[45]);
            btgRealtimeData.btTemp8 = getPropertyScalingValues("monomerTemperature", btTemp8);

            //TODO 功率
            int power = getNum(bytes[56], bytes[57]);
            System.out.println(power);
            btgRealtimeData.power = getPropertyScalingValues("power", power);
        }
    }

    //获取温度个数
    public static void getTempNum(BTGRealtimeData realtimeData, byte[] receiveArray) {
        if (realtimeData == null)
            realtimeData = new BTGRealtimeData();
        if (receiveArray == null || receiveArray.length == 0)
            return;
        realtimeData.tempNum = getNum(receiveArray[0], receiveArray[1]);
    }

    public static List<Integer> getAlarms(long alarm) {
        List<Integer> alarms = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            try {
                if ((alarm & (1 << i)) != 0) {//TODO 1
                    alarms.add(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return alarms;
    }

    public static List<Integer> getProtects(long protect) {
        List<Integer> protects = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            try {
                if ((protect & (1 << i)) != 0) {//TODO 1
                    protects.add(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return protects;
    }

    //获取电池均衡状态，任意一位为1即代表均衡开启,1代表均衡打开，0代表均衡关闭
    public static int[] getBatEqualizationStatus(long equalizationStatus) {
        int[] status = new int[16];
        for (int i = 0; i < 16; i++) {
            if ((equalizationStatus & (1 << i)) != 0) {//TODO 1
                status[i] = 1;
            } else status[i] = 0;

        }
        return status;
    }

    //获取mos状态（byte1：充电mos；byte2：放电mos；byte4：加热膜；byte5：放电限流(限流板使能)）
    public static List<Integer> getMosStatus(long mosStatus) {
        int chgMos = (mosStatus & (1 << 1)) != 0 ? 1 : 0;
        int disChgMos = (mosStatus & (1 << 2)) != 0 ? 1 : 0;
        int heatingFilm = (mosStatus & (1 << 4)) != 0 ? 1 : 0;
        int disChgCurrent = (mosStatus & (1 << 5)) != 0 ? 1 : 0;
        return Arrays.asList(chgMos, disChgMos, heatingFilm, disChgCurrent);
    }


    //获取单体电压高/低告警的电池号
    //获取单体电压高/低保护的电池号
    public static List<Integer> getVoltCellNos(long value) {
        List<Integer> cellNos = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            try {
                if ((value & (1 << i)) != 0) {//TODO 1
                    cellNos.add(i + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return cellNos;
    }


    //获取单体温度高/低告警的电池号
    public static List<Integer> getTempAlarmCellNos(long value) {
        List<Integer> cellNos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            try {
                if ((value & (1 << i)) != 0) {//TODO 1
                    cellNos.add(i + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return cellNos;
    }

    //获取单体温度高/低保护的电池号
    public static List<Integer> getTempProtectCellNos(long value) {
        List<Integer> cellNos = new ArrayList<>();
        for (int i = 4; i < 8; i++) {
            try {
                if ((value & (1 << i)) != 0) {//TODO 1
                    cellNos.add(i + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return cellNos;
    }

    public static void getBtData(BTGRealtimeData btgRealtimeData, byte[] receiveArray) {
        if (btgRealtimeData == null)
            btgRealtimeData = new BTGRealtimeData();
        if (receiveArray == null || receiveArray.length == 0)
            return;
        byte[] monomerVoltageBytes = ByteUtils.subByte(receiveArray, 0, 32);//前32个字节为16节单体单压数据
        byte[] monomerTemperatureBytes = ByteUtils.subByte(receiveArray, 32, 8);//后面8个字节为4节单体温度数据
        List<BTRealtimeData> batteryList = new ArrayList<BTRealtimeData>();
        btgRealtimeData.btRealtimeDataList = batteryList;
        for (int n = 0; n < 16; n++) {
            BTRealtimeData btRealtimeData = new BTRealtimeData();
            try {
                btRealtimeData.batName = (n + 1) + "#";
                int monomerVoltage = 0;//TODO 单体电压
                try {
                    monomerVoltage = getNum(monomerVoltageBytes[2 * n], monomerVoltageBytes[(2 * n) + 1]);//TODO 单体电压
                    btRealtimeData.monomerVoltage = getPropertyScalingValues("monomerVoltage", monomerVoltage);
                } catch (Exception e) {
                    e.printStackTrace();
                    btRealtimeData.monomerVoltage = new BigDecimal(monomerVoltage);
                }
                int monomerTemperature = 0;//TODO 单体温度
                if (2 * n < monomerTemperatureBytes.length) {
                    try {
                        monomerTemperature = getNum(monomerTemperatureBytes[2 * n], monomerTemperatureBytes[(2 * n) + 1]);//TODO 单体温度
                        btRealtimeData.monomerTemperature = getPropertyScalingValues("monomerTemperature", monomerTemperature);
                    } catch (Exception e) {
                        e.printStackTrace();
                        btRealtimeData.monomerTemperature = new BigDecimal(monomerTemperature);
                    }
                }
                batteryList.add(btRealtimeData);
            } catch (Exception e) {
                e.printStackTrace();
                batteryList.add(btRealtimeData);
                continue;
            }
        }
    }

    public static BigDecimal getPropertyScalingValues(String key, int orgValue) {
        BigDecimal value = new BigDecimal(0);
        // TODO 在JDK7以后，switch中的表达式可以为字符串了
        switch (key) {
            case "groupVoltage":
                if (orgValue != 0) {
                    value = new BigDecimal(orgValue).divide(ConstantKit.groupVoltageEnlargeMultiple, 1, BigDecimal.ROUND_HALF_UP);//TODO 组电压
                }
                break;
            case "groupElectricCurrent":
                if (orgValue != 0) {
                    value = (new BigDecimal(orgValue).add(ConstantKit.groupElectricCurrentOffset)).divide(ConstantKit.groupElectricCurrentEnlargeMultiple, 1, BigDecimal.ROUND_HALF_UP);//TODO 组充放电电流
                }
                break;
            case "mosTemp":
                if (orgValue != 0) {
                    value = (new BigDecimal(orgValue).add(ConstantKit.mosTempOffset)).divide(ConstantKit.mosTempEnlargeMultiple, 1, BigDecimal.ROUND_HALF_UP);//先偏移再除
                }
                break;
            case "envTemp":
                if (orgValue != 0) {
                    value = (new BigDecimal(orgValue).add(ConstantKit.temperature1Offset)).divide(ConstantKit.temperature1EnlargeMultiple, 1, BigDecimal.ROUND_HALF_UP);//先偏移再除
                }
                break;
            case "soc":
                if (orgValue != 0) {
                    value = new BigDecimal(orgValue).divide(ConstantKit.groupSOCLowestEnlargeMultiple, 1, BigDecimal.ROUND_HALF_UP);//TODO 按最低电池容量整组SOC
                }
                break;
            case "soh":
                if (orgValue != 0) {
                    value = new BigDecimal(orgValue).divide(ConstantKit.groupSOHLowestEnlargeMultiple, 1, BigDecimal.ROUND_HALF_UP);//TODO 按最低电池容量整组SOH
                }
                break;
            case "monomerVoltage":
                if (orgValue != 0) {
                    value = new BigDecimal(orgValue).divide(ConstantKit.monomerVoltageEnlargeMultiple, 3, BigDecimal.ROUND_HALF_UP);//TODO 单体电压
                }
                break;
            case "monomerTemperature":
                if (orgValue != 0) {
                    value = ((new BigDecimal(orgValue).add(ConstantKit.monomerTemperatureOffset)).divide(ConstantKit.monomerTemperatureEnlargeMultiple, 1, BigDecimal.ROUND_HALF_UP));//TODO 单体温度
                }
                break;
            case "averageDifferenceValue":
            case "rangeValue":
            case "rippleVoltage":
                value = new BigDecimal(orgValue);
                break;
            case "hydrogenContent":
                if (orgValue != 0) {
                    value = new BigDecimal(orgValue).divide(ConstantKit.hydrogenContentEnlargeMultiple, 0, BigDecimal.ROUND_HALF_UP);//TODO 氢气 浓度
                }
                break;
            case "capacity":
                if (orgValue != 0) {
                    value = new BigDecimal(orgValue).divide(ConstantKit.capacityEnlargeMultiple, 0, BigDecimal.ROUND_HALF_UP);//TODO 单体容量
                }
                break;
            case "power":
                if (orgValue != 0) {
                    value = new BigDecimal(orgValue).divide(ConstantKit.powerEnlargeMultiple, 1, BigDecimal.ROUND_HALF_UP);//TODO 功率
                }
                break;
            case "avgTemp":
                if (orgValue != 0) {
                    value = ((new BigDecimal(orgValue).add(ConstantKit.avgTempOffset)).divide(ConstantKit.avgTempEnlargeMultiple, 1, BigDecimal.ROUND_HALF_UP));//TODO 平均温度
                }
                break;
            default:
                value = new BigDecimal(0);
                break;
        }
        return value;
    }


    // TODO 16 转10计算
    public static int getNum(byte num1, byte num2) {
        byte[] array = new byte[]{num1, num2};
        int value = Integer.parseInt(ByteUtils.byteArrTo2Hex(array), 16);
        return value;
    }

    // TODO 16 转10计算
    public static int getNum(byte num1, byte num2, byte num3, byte num4) {
        byte[] array = new byte[]{num1, num2, num3, num4};
        int value = Integer.parseInt(ByteUtils.byteArrTo2Hex(array), 16);
        return value;
    }

    //获取数据上报时间
    public static void getDataReportTime(BTGRealtimeData realtimeData, byte[] receiveArray) {
        if (realtimeData == null)
            realtimeData = new BTGRealtimeData();
        if (receiveArray == null || receiveArray.length == 0)
            return;
        realtimeData.reportTime = ByteUtils.byteArrayToDate(receiveArray);//前六个字节位为上报时间，
    }


    //获取保护板软件版本号
    public static void getVersion(BTGRealtimeData realtimeData, byte[] receiveArray) {
        if (realtimeData == null)
            realtimeData = new BTGRealtimeData();
        if (receiveArray == null || receiveArray.length == 0)
            return;
        realtimeData.version = getNum(receiveArray[0], receiveArray[1]);
    }

    //获取充放电循环次数
    public static void getCycles(BTGRealtimeData realtimeData, byte[] receiveArray) {
        if (realtimeData == null)
            realtimeData = new BTGRealtimeData();
        if (receiveArray == null || receiveArray.length == 0)
            return;
        realtimeData.chgCycles = getNum(receiveArray[0], receiveArray[1]);
        realtimeData.disChgCycles = getNum(receiveArray[2], receiveArray[3]);
    }

    //获取单体容量
    public static void getCapacity(BTGRealtimeData realtimeData, byte[] receiveArray) {
        if (realtimeData == null)
            realtimeData = new BTGRealtimeData();
        if (receiveArray == null || receiveArray.length == 0)
            return;
        int capacity = getNum(receiveArray[0], receiveArray[1]);
        realtimeData.capacity = getPropertyScalingValues("capacity", capacity);
    }

    //获取告警信息/保护信息/电池状态/电池均衡
    public static void getAlarms(BTGRealtimeData realtimeData, byte[] receiveArray) {
        if (realtimeData == null)
            realtimeData = new BTGRealtimeData();
        if (receiveArray == null || receiveArray.length == 0)
            return;
        //告警信息
        long alarm = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[0], receiveArray[1], receiveArray[2], receiveArray[3]}), 16);
        List<Integer> alarmList = getAlarms(alarm);
        //保护信息
        long protects = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[4], receiveArray[5], receiveArray[6], receiveArray[7]}), 16);
        List<Integer> protectList = getProtects(protects);
        //电池状态 0为静置，1为放电，2为充电，3未充未放，4为加热，5为浮充
        int batStatus = getNum(receiveArray[8], receiveArray[9]);
        realtimeData.chargeStatus = batStatus;
        realtimeData.batEqualizationStatus = 0;
        realtimeData.fullChgProtection = protectList.contains(29) ? 1 : 0;//保护信息包含29.则认为开启满充保护
        realtimeData.alarms = alarmList;
        realtimeData.protects = protectList;

        //电池均衡状态
        //电池均衡状态  0~15位对应电池1到电池16的均衡状态，1表示在均衡，0表示未均衡。
        Long equalizationStatus = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[10], receiveArray[11]}), 16);
        int batEqualizationStatus[] = getBatEqualizationStatus(equalizationStatus);
        List<BTRealtimeData> batteryList = realtimeData.btRealtimeDataList;
        for (int n = 0; n < 16; n++) {
            BTRealtimeData btRealtimeData = batteryList.get(n);
            if (btRealtimeData == null) {
                btRealtimeData = new BTRealtimeData();
                btRealtimeData.batName = (n + 1) + "#";
                batteryList.add(btRealtimeData);
            }
            btRealtimeData.equilibriumState = batEqualizationStatus[n];
            if (batEqualizationStatus[n] == 1)
                realtimeData.batEqualizationStatus = 1;

        }
    }

    //查询mos开关状态
    public static void getMosStatus(BTGRealtimeData realtimeData, byte[] receiveArray) {
        if (realtimeData == null)
            realtimeData = new BTGRealtimeData();
        if (receiveArray == null || receiveArray.length == 0)
            return;
        long mosStatus = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[0], receiveArray[1]}), 16);
        List<Integer> mosStatusList = getMosStatus(mosStatus);
        if (mosStatusList != null && !mosStatusList.isEmpty()) {
            realtimeData.chgMos = mosStatusList.get(0);
            realtimeData.disChgMos = mosStatusList.get(1);
            realtimeData.heatingFilm = mosStatusList.get(2);
            realtimeData.disChgCurrent = mosStatusList.get(3);
        }
    }

    //查询具体的告警信息
    public static void getDetailAlarms(BTGRealtimeData realtimeData, byte[] receiveArray) {
        if (realtimeData == null)
            realtimeData = new BTGRealtimeData();
        if (receiveArray == null || receiveArray.length == 0)
            return;
        //单体电压高告警
        long highVoltAlarm = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[0], receiveArray[1]}), 16);
        List<Integer> highVoltAlarmCellNos = DataUtils.getVoltCellNos(highVoltAlarm);
        //单体电压高保护
        long highVoltProtect = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[2], receiveArray[3]}), 16);
        List<Integer> highVoltProtectCellNos = DataUtils.getVoltCellNos(highVoltProtect);
        //单体电压低告警
        long lowVoltAlarm = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[4], receiveArray[5]}), 16);
        List<Integer> lowVoltAlarmCellNos = DataUtils.getVoltCellNos(lowVoltAlarm);
        //单体电压低保护
        long lowVoltProtect = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[6], receiveArray[7]}), 16);
        List<Integer> lowVoltProtectCellNos = DataUtils.getVoltCellNos(lowVoltProtect);
        //单体温度高告警
        long highTempAlarm = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[8], receiveArray[9]}), 16);
        List<Integer> highTempAlarmCellNos = DataUtils.getTempAlarmCellNos(highTempAlarm);
        List<Integer> highTempProtectCellNos = DataUtils.getTempProtectCellNos(highTempAlarm);
        //单体温度低告警
        long lowTempAlarm = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[10], receiveArray[11]}), 16);
        List<Integer> lowTempAlarmCellNos = DataUtils.getTempAlarmCellNos(lowTempAlarm);
        List<Integer> lowTempProtectCellNos = DataUtils.getTempProtectCellNos(lowTempAlarm);

        realtimeData.alarmsCellNoMap.put(ConstantKit.TYPE_HIGH_VOLT_ALARM, highVoltAlarmCellNos);
        realtimeData.protectsCellNoMap.put(ConstantKit.TYPE_HIGH_VOLT_PROTECT, highVoltProtectCellNos);

        realtimeData.alarmsCellNoMap.put(ConstantKit.TYPE_LOW_VOLT_ALARM, lowVoltAlarmCellNos);
        realtimeData.protectsCellNoMap.put(ConstantKit.TYPE_LOW_VOLT_PROTECT, lowVoltProtectCellNos);

        realtimeData.alarmsCellNoMap.put(ConstantKit.TYPE_HIGH_TEMP_ALARM, highTempAlarmCellNos);
        realtimeData.protectsCellNoMap.put(ConstantKit.TYPE_HIGH_TEMP_PROTECT, highTempProtectCellNos);

        realtimeData.alarmsCellNoMap.put(ConstantKit.TYPE_LOW_TEMP_ALARM, lowTempAlarmCellNos);
        realtimeData.protectsCellNoMap.put(ConstantKit.TYPE_LOW_TEMP_PROTECT, lowTempProtectCellNos);
    }

}
