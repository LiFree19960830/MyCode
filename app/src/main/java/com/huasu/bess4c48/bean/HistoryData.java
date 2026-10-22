package com.huasu.bess4c48.bean;

public class HistoryData {
    // 基础信息
    public String updateTime;          // 更新时间
    public String event;               // 事件
    public String faultStatus;         // 故障状态

    // 核心电气参数
    public String totalV;        // 总电压（V）
    public String totalC;        // 总电流（A）
    public String soc;                    // SOC（剩余电量百分比）
    public String soh;                    // SOH（健康状态百分比）

    // 温度参数（单体温度1-8）
    public String cellT1;    // 单体温度1（℃）
    public String cellT2;    // 单体温度2（℃）
    public String cellT3;    // 单体温度3（℃）
    public String cellT4;    // 单体温度4（℃）
    public String cellT5;    // 单体温度5（℃）
    public String cellT6;    // 单体温度6（℃）
    public String cellT7;    // 单体温度7（℃）
    public String cellT8;    // 单体温度8（℃）

    // 电压极值与压差
    public String maxCellV;      // 最高单体电压（V）
    public String minCellV;      // 最低单体电压（V）
    public String maxVoltDiff;// 最大压差（V）

    // 其他温度参数
    public String mosT;      // MOS温度（℃）
    public String ambientT;  // 环境温度（℃）

    // 单体电压1-16
    public String cellV1;        // 单体电压1（V）
    public String cellV2;        // 单体电压2（V）
    public String cellV3;        // 单体电压3（V）
    public String cellV4;        // 单体电压4（V）
    public String cellV5;        // 单体电压5（V）
    public String cellV6;        // 单体电压6（V）
    public String cellV7;        // 单体电压7（V）
    public String cellV8;        // 单体电压8（V）
    public String cellV9;        // 单体电压9（V）
    public String cellV10;       // 单体电压10（V）
    public String cellV11;       // 单体电压11（V）
    public String cellV12;       // 单体电压12（V）
    public String cellV13;       // 单体电压13（V）
    public String cellV14;       // 单体电压14（V）
    public String cellV15;       // 单体电压15（V）
    public String cellV16;       // 单体电压16（V）

    // MOS状态
    public String CM;           // 充电MOS
    public String DCM;        // 放电MOS
    public String HM;          // 加热MOS
    public String CLM;     // 限流MOS

    // 运行状态
    public String BS;       // 均衡状态
    public String CDS;// 充放电状态

    // 累计数据
    public String DCC;    // 放电循环次数
    public String CTC; // 充电累计容量（Ah）
    public String DTC;// 放电累计容量（Ah）

    // 寄存器与状态
    public String IPS;         // IO引脚状态
    public String RCC;// 请求充电电流（A）
    public String ARV;  // 告警寄存器值
    public String PRV;// 保护寄存器值
    public String EAPRV;// 额外告警保护寄存器值

    // 故障与校验
    public String FS;        // 故障汇总
    public String CCR;      // CRC校验结果

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setFaultStatus(String faultStatus) {
        this.faultStatus = faultStatus;
    }

    public void setTotalV(String totalV) {
        this.totalV = totalV;
    }

    public void setTotalC(String totalC) {
        this.totalC = totalC;
    }

    public void setSoc(String soc) {
        this.soc = soc;
    }

    public void setSoh(String soh) {
        this.soh = soh;
    }

    public void setCellT1(String cellT1) {
        this.cellT1 = cellT1;
    }

    public void setCellT2(String cellT2) {
        this.cellT2 = cellT2;
    }

    public void setCellT3(String cellT3) {
        this.cellT3 = cellT3;
    }

    public void setCellT4(String cellT4) {
        this.cellT4 = cellT4;
    }

    public void setCellT5(String cellT5) {
        this.cellT5 = cellT5;
    }

    public void setCellT6(String cellT6) {
        this.cellT6 = cellT6;
    }

    public void setCellT7(String cellT7) {
        this.cellT7 = cellT7;
    }

    public void setCellT8(String cellT8) {
        this.cellT8 = cellT8;
    }

    public void setMaxCellV(String maxCellV) {
        this.maxCellV = maxCellV;
    }

    public void setMinCellV(String minCellV) {
        this.minCellV = minCellV;
    }

    public void setMaxVoltDiff(String maxVoltDiff) {
        this.maxVoltDiff = maxVoltDiff;
    }

    public void setMosT(String mosT) {
        this.mosT = mosT;
    }

    public void setAmbientT(String ambientT) {
        this.ambientT = ambientT;
    }

    public void setCellV1(String cellV1) {
        this.cellV1 = cellV1;
    }

    public void setCellV2(String cellV2) {
        this.cellV2 = cellV2;
    }

    public void setCellV3(String cellV3) {
        this.cellV3 = cellV3;
    }

    public void setCellV4(String cellV4) {
        this.cellV4 = cellV4;
    }

    public void setCellV5(String cellV5) {
        this.cellV5 = cellV5;
    }

    public void setCellV6(String cellV6) {
        this.cellV6 = cellV6;
    }

    public void setCellV7(String cellV7) {
        this.cellV7 = cellV7;
    }

    public void setCellV8(String cellV8) {
        this.cellV8 = cellV8;
    }

    public void setCellV9(String cellV9) {
        this.cellV9 = cellV9;
    }

    public void setCellV10(String cellV10) {
        this.cellV10 = cellV10;
    }

    public void setCellV11(String cellV11) {
        this.cellV11 = cellV11;
    }

    public void setCellV12(String cellV12) {
        this.cellV12 = cellV12;
    }

    public void setCellV13(String cellV13) {
        this.cellV13 = cellV13;
    }

    public void setCellV14(String cellV14) {
        this.cellV14 = cellV14;
    }

    public void setCellV15(String cellV15) {
        this.cellV15 = cellV15;
    }

    public void setCellV16(String cellV16) {
        this.cellV16 = cellV16;
    }

    public void setCM(String CM) {
        this.CM = CM;
    }

    public void setDCM(String DCM) {
        this.DCM = DCM;
    }

    public void setHM(String HM) {
        this.HM = HM;
    }

    public void setCLM(String CLM) {
        this.CLM = CLM;
    }

    public void setBS(String BS) {
        this.BS = BS;
    }

    public void setCDS(String CDS) {
        this.CDS = CDS;
    }

    public void setDCC(String DCC) {
        this.DCC = DCC;
    }

    public void setCTC(String CTC) {
        this.CTC = CTC;
    }

    public void setDTC(String DTC) {
        this.DTC = DTC;
    }

    public void setIPS(String IPS) {
        this.IPS = IPS;
    }

    public void setRCC(String RCC) {
        this.RCC = RCC;
    }

    public void setARV(String ARV) {
        this.ARV = ARV;
    }

    public void setPRV(String PRV) {
        this.PRV = PRV;
    }

    public void setEAPRV(String EAPRV) {
        this.EAPRV = EAPRV;
    }

    public void setFS(String FS) {
        this.FS = FS;
    }

    public void setCCR(String CCR) {
        this.CCR = CCR;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getEvent() {
        return event;
    }

    public String getFaultStatus() {
        return faultStatus;
    }

    public String getTotalV() {
        return totalV;
    }

    public String getTotalC() {
        return totalC;
    }

    public String getSoc() {
        return soc;
    }

    public String getSoh() {
        return soh;
    }

    public String getCellT1() {
        return cellT1;
    }

    public String getCellT2() {
        return cellT2;
    }

    public String getCellT3() {
        return cellT3;
    }

    public String getCellT4() {
        return cellT4;
    }

    public String getCellT5() {
        return cellT5;
    }

    public String getCellT6() {
        return cellT6;
    }

    public String getCellT7() {
        return cellT7;
    }

    public String getCellT8() {
        return cellT8;
    }

    public String getMaxCellV() {
        return maxCellV;
    }

    public String getMinCellV() {
        return minCellV;
    }

    public String getMaxVoltDiff() {
        return maxVoltDiff;
    }

    public String getMosT() {
        return mosT;
    }

    public String getAmbientT() {
        return ambientT;
    }

    public String getCellV1() {
        return cellV1;
    }

    public String getCellV2() {
        return cellV2;
    }

    public String getCellV3() {
        return cellV3;
    }

    public String getCellV4() {
        return cellV4;
    }

    public String getCellV5() {
        return cellV5;
    }

    public String getCellV6() {
        return cellV6;
    }

    public String getCellV7() {
        return cellV7;
    }

    public String getCellV8() {
        return cellV8;
    }

    public String getCellV9() {
        return cellV9;
    }

    public String getCellV10() {
        return cellV10;
    }

    public String getCellV11() {
        return cellV11;
    }

    public String getCellV12() {
        return cellV12;
    }

    public String getCellV13() {
        return cellV13;
    }

    public String getCellV14() {
        return cellV14;
    }

    public String getCellV15() {
        return cellV15;
    }

    public String getCellV16() {
        return cellV16;
    }

    public String getCM() {
        return CM;
    }

    public String getDCM() {
        return DCM;
    }

    public String getHM() {
        return HM;
    }

    public String getCLM() {
        return CLM;
    }

    public String getBS() {
        return BS;
    }

    public String getCDS() {
        return CDS;
    }

    public String getDCC() {
        return DCC;
    }

    public String getCTC() {
        return CTC;
    }

    public String getDTC() {
        return DTC;
    }

    public String getIPS() {
        return IPS;
    }

    public String getRCC() {
        return RCC;
    }

    public String getARV() {
        return ARV;
    }

    public String getPRV() {
        return PRV;
    }

    public String getEAPRV() {
        return EAPRV;
    }

    public String getFS() {
        return FS;
    }

    public String getCCR() {
        return CCR;
    }
}
