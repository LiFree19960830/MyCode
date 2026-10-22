package com.huasu.bess4c48.bean;


import java.math.BigDecimal;

/**
 * 电池信息
 */
public class BTRealtimeData {

    public String batName;
    public Long reportTime;
    public Short batteryStatus;//电池告警状态(0 正常，1 告警，2 故障)
    public BigDecimal monomerVoltage;  //TODO 电压
    public BigDecimal monomerTemperature;
    public long equilibriumState;//均衡状态

    public boolean isEquilibriuming(){
        return equilibriumState==1;
    }

}
