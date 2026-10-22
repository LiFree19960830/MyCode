package com.huasu.bess4c48.bean;

public class CommandData {

    //查询保护板时间
    public static final int command_type_time = 1;
    //查询程序子版本号
    public static final int command_type_version = 2;
    //查询电池组属性
    public static final int command_type_btg = 3;
    //查询电池电压/温度
    public static final int command_type_bat = 4;
    //查询充电循环次数/放电循环次
    public static final int command_type_cycle_count = 5;
    //查询单体容量指令
    public static final int command_type_capacity = 6;
    //查询告警内容指令/保护内容/电池状态/电池均衡
    public static final int command_type_alarm = 7;
    //查询mos开关（充电mos、放电mos、加热膜、充电限流）
    public static final int command_type_mos = 8;
    //查询单体电压高告警、单体电压高保护、单体电压低告警、单体电压低保护、单体温度高告警/保护、单体温度低告警/保护
    public static final int command_type_detail_alarm = 9;
    //查询电池基本信息（电池类型、电池串数、电池容量、温度个数）
    public static final int command_type_bat_param = 10;
    //查询电池电压参数基本信息（单体电压高/低/不均,总压高/低）
    public static final int command_type_bat_volt_param = 11;
    //查询电池电压参数基本信息（电芯失效）
    public static final int command_type_bat_failure_param = 12;
    //查询电池电流参数基本信息（充电/放电过流）
    public static final int command_type_bat_current_param = 13;
    //查询电池温度参数基本信息
    public static final int command_type_bat_temp_param1 = 14;
    public static final int command_type_bat_temp_param2 = 15;
    //查询pcs参数基本信息
    public static final int command_type_pcs_param = 16;
    //查询功能参数基本信息
    public static final int command_type_function_param1 = 17;
    public static final int command_type_function_param2 = 18;
    public static final int command_type_function_param3 = 19;
    public static final int command_type_function_param4 = 20;
    public static final int command_type_function_param5 = 37;
    //设置参数
    public static final int command_type_setting = 21;
    //读取参数
    public static final int command_type_reading = 22;
    //恢复默认参数
    public static final int command_type_restore = 23;
    //读取历史数据
    public static final int command_type_read_history_data = 24;
    public static final int command_type_read_history_data_count = 25;
    //设备管理
    public static final int command_type_device_manage_param1 = 26;
    public static final int command_type_device_manage_param2 = 27;
    public static final int command_type_device_manage_param3 = 28;
    public static final int command_type_device_manage_param4 = 29;
    //升级管理
    public static final int command_type_upgrade_param1 = 30;
    public static final int command_type_upgrade_param2 = 31;
    //升级-启动
    public static final int command_type_upgrade_start = 32;
    //升级-升级包信息
    public static final int command_type_upgrade_package = 33;
    //升级-升级
    public static final int command_type_upgrade_upgrade = 34;
    //升级-升级结果
    public static final int command_type_upgrade_result = 35;
    //查询温度个数指令
    public static final int command_type_temp_num = 36;
    //命令类型
    public int commandType;
    //命令
    public byte[] command;

    //只读
    public boolean isRead;
    //是否升级
    public boolean isUpgrade;
    //写入
    public boolean isWrite;
    public String commandTypeStr;

    //是否弹出提示框
    public boolean isShowToast;

    //下设的参数
    public int parentPosition;
    public int childPosition;

    public CommandData(int commandType, byte[] command) {
        this.commandType = commandType;
        this.command = command;
    }

    public CommandData(int commandType, byte[] command, String str, boolean isRead, boolean isWrite, boolean isUpgrade) {
        this.commandType = commandType;
        this.command = command;
        this.commandTypeStr = str;
        this.isRead = isRead;
        this.isWrite = isWrite;
        this.isUpgrade = isUpgrade;
    }

    public CommandData(int commandType, byte[] command, String str, boolean isRead, boolean isWrite, boolean isUpgrade, boolean isShowToast) {
        this.commandType = commandType;
        this.command = command;
        this.commandTypeStr = str;
        this.isRead = isRead;
        this.isWrite = isWrite;
        this.isUpgrade = isUpgrade;
        this.isShowToast = isShowToast;
    }


}
