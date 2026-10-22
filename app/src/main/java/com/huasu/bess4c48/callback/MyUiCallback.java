package com.huasu.bess4c48.callback;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.BTGRealtimeData;
import com.huasu.bess4c48.bean.CommandData;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.BleHelper;
import com.huasu.bess4c48.utils.ByteUtils;
import com.huasu.bess4c48.utils.CPResourceUtil;
import com.huasu.bess4c48.utils.DataUtils;
import com.huasu.bess4c48.utils.InstructionUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 现在的重发指令只针对实际的数据请求指令
 * 发送addr指令和ok指令还需要开启重发指令的操作吗
 */
public class MyUiCallback implements UiCallback {
    private static final String TAG = MyUiCallback.class.getSimpleName();
    private ConcurrentLinkedDeque<CommandData> commands;//命令集合
    private BTGRealtimeData btgRealtimeData;//电池组对象
    private CommandData mCurrentCommand;//当前正在执行的指令
    private byte[] lastReceiveArray;//上次接收到的数据
    private int readLength, queryLength;
    private int sendCommandDelayMillis = 5000; //再次发送指令延时
    private int receiveDataDelayMillis = 5000; //接收数据处理延时
    private int mSendCommandTimes = 0;//发送获取电池数据指令次数
    private boolean isCancel = false;

    private static class SingletonHolder {
        private static final MyUiCallback INSTANCE = new MyUiCallback();
    }

    //获取单例
    public static MyUiCallback getInstance() {

        return SingletonHolder.INSTANCE;
    }

    public MyUiCallback() {
    }

    @Override
    public void state(String state) {
        Log.e(TAG, "蓝牙连接状态改变" + state + " " + this);
        handler.removeCallbacksAndMessages(null);
        isCancel = true;
    }

    @Override
    public void connectFail() {
    }

    @Override
    public void connectSuccess() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //蓝牙连接成功后，开始发送查询数据指令
        handler.removeCallbacksAndMessages(null);
        isCancel = false;
        initCommands();
        lastReceiveArray = null;
        readLength = 0;
        mCurrentCommand = commands.poll();

        BleHelper.getInstance().sendCommand(mCurrentCommand.command, mCurrentCommand.isUpgrade);
        queryLength = ByteUtils.hex2ToTempera(ByteUtils.byte2Hex(mCurrentCommand.command[5]));
        Log.e(TAG, "连接成功，开始发送查询指令，发送内容: " + Arrays.toString(mCurrentCommand.command));
        //每次发送指令都要触发一次计时器，为了做一段时间内没有收到数据后重发指令的操作
        handler.postDelayed(sendCommandAgainRunnable, sendCommandDelayMillis);
        mSendCommandTimes = 0;
    }

    private Handler handler = new Handler();
    //发送指令后，一段时间内没有收到数据的逻辑处理；重新发送指令
    private Runnable sendCommandAgainRunnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(this);
            if (isCancel) {
                return;
            }
            //超过5次，发送下一条指令
            if (mSendCommandTimes == 5) {
                EventBus.getDefault().post(new EventManager.OnConnectFailEvent());
                sendNextCommand();
                return;
            }

            sendCommandAgain();
            handler.postDelayed(sendCommandAgainRunnable, sendCommandDelayMillis);
            mSendCommandTimes++;
        }
    };


    //收到数据后，一定时间内数据没有接收完的逻辑处理；重新发送指令
    private Runnable receiveDataRunnable = new Runnable() {
        @Override
        public void run() {
            if (isCancel) {
                handler.removeCallbacks(this);
                return;
            }
            sendCommandAgain();
            handler.removeCallbacks(this);
            handler.postDelayed(sendCommandAgainRunnable, sendCommandDelayMillis);
            mSendCommandTimes = 0;
        }
    };

    long startTime = 0l, diff = 100l;
    boolean isCrcResult = false;

    @Override
    public void showReceiveData(final byte[] receiveData) {
        String content = ByteUtils.bytesToHexString(receiveData);
        Log.d(TAG, "收到内容: " + mCurrentCommand.commandType + ";" + content + ";" + receiveData.length);
        if (mCurrentCommand.commandType == CommandData.command_type_reading) {
            Log.d(TAG, "收到: " + mCurrentCommand.commandTypeStr + ";响应内容" + content + ";" + receiveData.length);
        }
        try {
            if (receiveData == null)
                return;
            //收到保护板返回的电池数据消息，取消重发指令定时器
            handler.removeCallbacks(sendCommandAgainRunnable);
            //如果上次读到的数据为空
            if (lastReceiveArray == null) {
                lastReceiveArray = receiveData;
                readLength = ByteUtils.hex2ToTempera(ByteUtils.byte2Hex(receiveData[2]));
                //收到第一包电池数据后，开启接收数据定时器处理，理论上认为1s内可以把一条指令返回的数据接收完全，
                // 如果1s内还接收不完，那么认为该指令返回的数据有问题，需要再次发送该指令
                handler.postDelayed(receiveDataRunnable, receiveDataDelayMillis);
            } else {
                lastReceiveArray = ByteUtils.unitByteArray(lastReceiveArray, receiveData);
            }

            //如果是升级指令
            if (mCurrentCommand.isUpgrade) {
                Log.e(TAG, "收到  " + mCurrentCommand.commandTypeStr + "指令相应内容=" + content + ";长度=" + receiveData.length);
                //下发启动指令消息响应
                if (mCurrentCommand.commandType == CommandData.command_type_upgrade_start) {
                    handleUpgradeStart();
                }
                //下发升级包信息响应
                if (mCurrentCommand.commandType == CommandData.command_type_upgrade_package) {
                    handleUpgradePackage();
                }
                //下发升级信息响应
                if (mCurrentCommand.commandType == CommandData.command_type_upgrade_upgrade) {
                    handleUpgrade();
                }
                //查询升级结果
                if (mCurrentCommand.commandType == CommandData.command_type_upgrade_result) {
                    if (lastReceiveArray == null || lastReceiveArray.length < 11)
                        return;
                    else {
                        //数据接收完，取消数据接收处理逻辑定时器
                        handler.removeCallbacks(receiveDataRunnable);
                        if (isTrue(lastReceiveArray[0], "AA") && isTrue(lastReceiveArray[1], "55") &&
                                isTrue(lastReceiveArray[2], "00") && isTrue(lastReceiveArray[3], "13")) {
                            //lastReceiveArray[8]
                            //00、升级成功；
                            //01、升级失败。
                            //发送升级失败或者成功消息
                            EventBus.getDefault().post(new EventManager.OnReceiveUpgradeResult(isTrue(lastReceiveArray[8], "00")));
                        } else
                            EventBus.getDefault().post(new EventManager.OnReceiveUpgradeResult(false));
                        sendNextCommand();
                    }
                }
                return;
            }
            //如果是写入指令
            if (mCurrentCommand.isWrite) {
                //长度为8代表设置成功，长度为5代表设置失败，失败固定回复FA 90 02 BC 30
                if (lastReceiveArray.length == 8 || lastReceiveArray.length == 5) {
                    //数据接收完，取消数据接收处理逻辑定时器
                    handler.removeCallbacks(receiveDataRunnable);
                    Log.e(TAG, "收到  " + mCurrentCommand.commandTypeStr + "指令相应内容=" + content + ";长度=" + receiveData.length);
                    if (mCurrentCommand.commandType == CommandData.command_type_restore)
                        EventBus.getDefault().post(new EventManager.OnRestoreDeviceParamEvent(lastReceiveArray.length == 8, mCurrentCommand.isShowToast));
                    else
                        EventBus.getDefault().post(new EventManager.OnShowSettingDeviceParamEvent(lastReceiveArray.length == 8, mCurrentCommand.isShowToast, mCurrentCommand.parentPosition, mCurrentCommand.childPosition));

                    sendNextCommand();
                }
                return;
            }
            if (lastReceiveArray.length != readLength + 5) {
                return;
            }

            Log.e(TAG, "收到  " + mCurrentCommand.commandTypeStr + "指令相应内容=" + content + ";长度=" + receiveData.length);
            //数据接收完，取消数据接收处理逻辑定时器
            handler.removeCallbacks(receiveDataRunnable);
            //对数据做长度校验，长度校验不通过，发送下一条数据查询指令
            if (queryLength * 2 != readLength && mCurrentCommand.commandType != CommandData.command_type_read_history_data) {
                Log.e(TAG, "读取" + mCurrentCommand.commandTypeStr + "指令数据出现错误；长度校验失败；指令=" +
                        ByteUtils.bytesToHexString(mCurrentCommand.command) + "；返回数据=" + ByteUtils.bytesToHexString(lastReceiveArray));
                sendNextCommand();
                return;
            }
            //对数据做crc校验
            byte[] waitCrcDataArray = ByteUtils.subByte(lastReceiveArray, 0, lastReceiveArray.length - 2);
            String myDataCrc = Arrays.toString(InstructionUtil.packageInstruction(waitCrcDataArray));
            String receiveStr = Arrays.toString(lastReceiveArray);
            //对返回的数据做完crc校验后跟原始数据不一致，那么认为该包数据异常
            if (!myDataCrc.equals(receiveStr)) {
                Log.e(TAG, "读取" + mCurrentCommand.commandTypeStr + "指令数据出现错误；crc校验失败；指令=" +
                        ByteUtils.bytesToHexString(mCurrentCommand.command) + "；返回数据=" + ByteUtils.bytesToHexString(lastReceiveArray));
                if (CommandData.command_type_read_history_data == mCurrentCommand.commandType) {
                    EventBus.getDefault().post(new EventManager.OnShowHistoryDataFailEvent());
                }
                sendNextCommand();
                return;
            }

            //如果是读取历史数据,需要再额外做一次crc校验
            if (CommandData.command_type_read_history_data == mCurrentCommand.commandType) {
                waitCrcDataArray = ByteUtils.subByte(lastReceiveArray, 3, lastReceiveArray.length - 7);
                myDataCrc = ByteUtils.bytesToHexString(InstructionUtil.packageInstruction(waitCrcDataArray, true));
                receiveStr = ByteUtils.bytesToHexString(ByteUtils.subByte(lastReceiveArray, 3, lastReceiveArray.length - 5));
                Log.e("AAA", myDataCrc + "\n" + receiveStr + "\n" + ByteUtils.bytesToHexString(lastReceiveArray));
                if (!myDataCrc.equals(receiveStr)) {
                    Log.e(TAG, "读取" + mCurrentCommand.commandTypeStr + "指令数据出现错误；crc校验失败；指令=" +
                            ByteUtils.bytesToHexString(mCurrentCommand.command) + "；返回数据=" + ByteUtils.bytesToHexString(lastReceiveArray));
                    isCrcResult = false;
                } else
                    isCrcResult = true;
            }
            byte[] receiveArray = ByteUtils.subByte(lastReceiveArray, 3, readLength);
            if (btgRealtimeData == null)
                btgRealtimeData = new BTGRealtimeData();
            btgRealtimeData.diff = "" + diff;
            switch (mCurrentCommand.commandType) {
                case CommandData.command_type_time://解析保护板时间
                    DataUtils.getDataReportTime(btgRealtimeData, receiveArray);
                    Log.d(TAG, "保护板时间: " + btgRealtimeData.reportTime);
                    break;
                case CommandData.command_type_version://解析程序子版本号
                    DataUtils.getVersion(btgRealtimeData, receiveArray);
                    Log.d(TAG, "程序子版本号: " + btgRealtimeData.version);
                    break;
                case CommandData.command_type_btg://解析电池组数据
                    DataUtils.getBTGData(btgRealtimeData, receiveArray);
                    Log.d(TAG, "电池组信息为: " + new Gson().toJson(btgRealtimeData));
                    break;
                case CommandData.command_type_temp_num://解析温度个数
                    DataUtils.getTempNum(btgRealtimeData, receiveArray);
                    Log.d(TAG, "电池组信息为: " + new Gson().toJson(btgRealtimeData));
                    break;
                case CommandData.command_type_bat://解析电池信息
                    DataUtils.getBtData(btgRealtimeData, receiveArray);
                    Log.d(TAG, "电池信息为: " + new Gson().toJson(btgRealtimeData.btRealtimeDataList));
                    break;
                case CommandData.command_type_cycle_count://充电循环次数/放电循环次数指令
                    DataUtils.getCycles(btgRealtimeData, receiveArray);
                    Log.d(TAG, "充电次数/放电次数: " + btgRealtimeData.chgCycles + "/" + btgRealtimeData.disChgCycles);
                    break;
                case CommandData.command_type_capacity://单体容量
                    DataUtils.getCapacity(btgRealtimeData, receiveArray);
                    Log.d(TAG, "单体容量: " + btgRealtimeData.capacity);
                    break;
                case CommandData.command_type_alarm://告警信息/保护信息/电池状态/电池均衡
                    DataUtils.getAlarms(btgRealtimeData, receiveArray);
                    Log.d(TAG, "告警信息: " + new Gson().toJson(btgRealtimeData.alarms)
                            + ";保护信息: " + new Gson().toJson(btgRealtimeData.protects)
                            + ";电池状态: " + btgRealtimeData.chargeStatus
                            + ";均衡状态: " + btgRealtimeData.batEqualizationStatus
                            + ";满充保护状态: " + btgRealtimeData.fullChgProtection);
                    break;
                case CommandData.command_type_mos://查询mos开关状态
                    DataUtils.getMosStatus(btgRealtimeData, receiveArray);
                    Log.d(TAG, "mos开关状态: " + btgRealtimeData.chgMos + ";" + btgRealtimeData.disChgMos + ";" + btgRealtimeData.heatingFilm + ";" + btgRealtimeData.disChgCurrent);

                    break;
                case CommandData.command_type_detail_alarm://查询具体的告警信息
                    DataUtils.getDetailAlarms(btgRealtimeData, receiveArray);
                    Log.d(TAG, "发生告警的电池编号: " + new Gson().toJson(btgRealtimeData.alarmsCellNoMap) + ";"
                            + "发生保护的电池编号：" + new Gson().toJson(btgRealtimeData.protectsCellNoMap));
                    break;
                case CommandData.command_type_bat_param://查询电池基本信息（电池类型、电池串数、电池容量、温度个数）
                case CommandData.command_type_bat_volt_param://查询电池电压参数基本信息（单体电压高/低/不均,总压高/低）
                case CommandData.command_type_bat_failure_param://查询电池电压参数基本信息（电芯失效）
                case CommandData.command_type_bat_current_param: //查询电池电流参数基本信息（充电/放电过流）
                case CommandData.command_type_bat_temp_param1: //查询电池温度类参数
                case CommandData.command_type_bat_temp_param2: //查询电池温度类参数
                case CommandData.command_type_pcs_param: //查询pcs参数
                case CommandData.command_type_function_param1: //查询功能参数
                case CommandData.command_type_function_param2: //查询功能参数
                case CommandData.command_type_function_param3: //查询功能参数
                case CommandData.command_type_function_param4: //查询功能参数
                case CommandData.command_type_function_param5: //查询功能参数
                case CommandData.command_type_read_history_data_count: //读取到历史数据总条数
                case CommandData.command_type_device_manage_param1: //设备管理1
                case CommandData.command_type_device_manage_param2: //设备管理2
                case CommandData.command_type_device_manage_param3: //设备管理3
                case CommandData.command_type_device_manage_param4: //设备管理4
                case CommandData.command_type_upgrade_param2://升级管理1
                case CommandData.command_type_upgrade_param1://升级管理2
                    EventBus.getDefault().post(new EventManager.OnShowDeviceParamEvent(mCurrentCommand.commandType, receiveArray, mCurrentCommand.isShowToast));
                    break;
                case CommandData.command_type_reading: //读取一个参数
                    EventBus.getDefault().post(new EventManager.OnShowReadOneDeviceParamEvent(receiveArray, mCurrentCommand.parentPosition, mCurrentCommand.childPosition));
                    break;
                case CommandData.command_type_read_history_data: //读取到历史数据
                    EventBus.getDefault().post(new EventManager.OnShowHistoryDataEvent(receiveArray, isCrcResult));
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendNextCommand();
    }

    private boolean isTrue(byte item, String val) {
        return val.equals(InstructionUtil.byte2Hex(item));
    }

    //处理启动指令消息响应
    private void handleUpgradeStart() {
        if (lastReceiveArray.length < 10)
            return;
        else {
            //数据接收完，取消数据接收处理逻辑定时器
            handler.removeCallbacks(receiveDataRunnable);
            if (isTrue(lastReceiveArray[0], "AA") && isTrue(lastReceiveArray[1], "55") &&
                    isTrue(lastReceiveArray[2], "00") && isTrue(lastReceiveArray[3], "10")) {
                //保护板进入等待升级模式
                //发送广播（发送升级包消息）
                EventBus.getDefault().post(new EventManager.OnSendUpgradePackageStart());
            } else {
                //保护板回复消息失败
                //发送升级失败广播，停止升级
                EventBus.getDefault().post(new EventManager.OnSendUpgradeFail(R.string.upgrade_fail_for_refuse));
                sendNextCommand();
            }
        }
    }

    //处理发送升级包指令消息响应
    private void handleUpgradePackage() {
        if (lastReceiveArray == null || lastReceiveArray.length < 12)
            return;
        else {
            //数据接收完，取消数据接收处理逻辑定时器
            handler.removeCallbacks(receiveDataRunnable);
            if (isTrue(lastReceiveArray[0], "AA") && isTrue(lastReceiveArray[1], "55") &&
                    isTrue(lastReceiveArray[2], "00") && isTrue(lastReceiveArray[3], "11")) {
                if (isTrue(lastReceiveArray[8], "00")) {//表示可以下载
                    //发送升级文件信息，需要传递lastReceiveArray[9] 的数据
                    int packageWord = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{0x00, lastReceiveArray[9]}), 16);
                    EventBus.getDefault().post(new EventManager.OnSendStartUpgrade(packageWord));
                } else {
                    //拒绝升级（升级包太大）
                    //发送升级失败广播，停止升级（升级包太大）
                    EventBus.getDefault().post(new EventManager.OnSendUpgradeFail(R.string.upgrade_fail_for_big));
                    sendNextCommand();
                }
            } else {
                //保护板回复消息失败
                //发送升级失败广播，停止升级
                EventBus.getDefault().post(new EventManager.OnSendUpgradeFail(R.string.upgrade_fail_for_refuse));
                sendNextCommand();
            }
        }
    }

    //处理发送升级指令消息响应
    private void handleUpgrade() {
        if (lastReceiveArray == null || lastReceiveArray.length < 11)
            return;
        else {
            //数据接收完，取消数据接收处理逻辑定时器
            handler.removeCallbacks(receiveDataRunnable);
            if (isTrue(lastReceiveArray[0], "AA") && isTrue(lastReceiveArray[1], "55") &&
                    isTrue(lastReceiveArray[2], "00") && isTrue(lastReceiveArray[3], "12")) {
                //lastReceiveArray[8]
                //00、终端校验升级包通过回复继续下发；
                //01、校验不通过回复重发指令；
                //02、超过失败次数，回复停止下载；其他保留。
                byte byte8 = lastReceiveArray[8];
                int msgCode = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{0x00, byte8}), 16);
                EventBus.getDefault().post(new EventManager.OnReceiveUpgradeProcessMsg(msgCode));
                if (isTrue(byte8, "02")) {
                    sendNextCommand();
                }
            } else {
                //保护板回复消息失败
                //发送升级失败广播，停止升级
                EventBus.getDefault().post(new EventManager.OnSendUpgradeFail(R.string.upgrade_fail_for_refuse));
                sendNextCommand();
            }
        }
    }

    //发送下一条指令
    private void sendNextCommand() {
        lastReceiveArray = null;
        readLength = 0;
        //数据全部读取完成，延时1秒取下一次数据
        if (commands.isEmpty()) {
            initCommands();
            EventBus.getDefault().post(new EventManager.OnShowBtgRealtimeDataEvent(btgRealtimeData));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            btgRealtimeData = new BTGRealtimeData();
        }
        if (commands.isEmpty())
            return;
        mCurrentCommand = commands.poll();
        BleHelper.getInstance().sendCommand(mCurrentCommand.command, mCurrentCommand.isUpgrade);
        startTime = System.currentTimeMillis();
        queryLength = ByteUtils.hex2ToTempera(ByteUtils.byte2Hex(mCurrentCommand.command[5]));
        Log.e(TAG, "发送 " + mCurrentCommand.commandTypeStr + "指令，指令内容=" +
                ByteUtils.bytesToHexString(InstructionUtil.packageInstruction(mCurrentCommand.command, mCurrentCommand.isUpgrade)));
        handler.postDelayed(sendCommandAgainRunnable, sendCommandDelayMillis);
        mSendCommandTimes = 0;
    }


    //发送下一条指令
    private void sendUpgradeCommand() {
        lastReceiveArray = null;
        mCurrentCommand = commands.poll();
        BleHelper.getInstance().sendCommand(mCurrentCommand.command, mCurrentCommand.isUpgrade);
        Log.e(TAG, "发送 " + mCurrentCommand.commandTypeStr + "指令，指令内容=" +
                ByteUtils.bytesToHexString(InstructionUtil.packageInstruction(mCurrentCommand.command, mCurrentCommand.isUpgrade)));
        handler.postDelayed(sendCommandAgainRunnable, sendCommandDelayMillis);
        mSendCommandTimes = 0;
    }

    //重发当前指令
    private void sendCommandAgain() {
        lastReceiveArray = null;
        readLength = 0;
        BleHelper.getInstance().sendCommand(mCurrentCommand.command, mCurrentCommand.isUpgrade);
        queryLength = ByteUtils.hex2ToTempera(ByteUtils.byte2Hex(mCurrentCommand.command[5]));
        Log.e(TAG, "重新发送" + mCurrentCommand.commandTypeStr + " 指令，指令内容=" + ByteUtils.bytesToHexString(InstructionUtil.packageInstruction(mCurrentCommand.command, mCurrentCommand.isUpgrade)));
    }

    private void initCommands() {
        if (commands == null)
            commands = new ConcurrentLinkedDeque<>();
        commands.clear();
        byte index = (byte) 0xfa;
        //byte index = 11111010;
        byte[] queryTime = new byte[]{index, 0x03, 0x00, (byte) 0xC0, 0x00, 0x03};//TODO 查询保护板时间数据指令
        byte[] queryVersion = new byte[]{index, 0x03, 0x00, (byte) 0x0B, 0x00, 0x01};//TODO 查询程序子版本号数据指令
        byte[] queryBTGAttribute = new byte[]{index, 0x03, 0x01, (byte) 0x10, 0x00, 0x1D};//TODO 查询电池组属性测量数据指令
        byte[] queryTempNum = new byte[]{index, 0x03, 0x00, (byte) 0x65, 0x00, 0x01};//TODO 查询温度个数指令
        byte[] queryBtAttribute = new byte[]{index, 0x03, 0x00, (byte) 0xFC, 0x00, 0x14};//TODO 查询电池电压/温度测量数据指令
        byte[] queryChgCycles = new byte[]{index, 0x03, 0x00, (byte) 0xD2, 0x00, 0x02};//TODO 查询充电循环次数/放电循环次数指令
        byte[] queryCapacity = new byte[]{index, 0x03, 0x00, (byte) 0x66, 0x00, 0x01};//TODO 查询单体容量指令
        byte[] queryAlarms = new byte[]{index, 0x03, 0x01, (byte) 0x31, 0x00, 0x06};//TODO 查询告警内容指令/保护内容/电池状态/电池均衡指令
        byte[] queryMosStatus = new byte[]{index, 0x03, 0x01, (byte) 0x2D, 0x00, 0x01};//TODO 查询mos开关（充电mos、放电mos、加热膜、充电限流）指令
        byte[] queryAlarmDetailInfo = new byte[]{index, 0x03, 0x01, (byte) 0x3A, 0x00, 0x06};//TODO 查询单体电压高告警、单体电压高保护、单体电压低告警、单体电压低保护、单体温度高告警/保护、单体温度低告警/保护
        commands.add(new CommandData(CommandData.command_type_time, queryTime, "查询保护板时间", true, false, false));
        commands.add(new CommandData(CommandData.command_type_version, queryVersion, "查询程序子版本号", true, false, false));
        commands.add(new CommandData(CommandData.command_type_btg, queryBTGAttribute, "查询池组属性", true, false, false));
        commands.add(new CommandData(CommandData.command_type_temp_num, queryTempNum, "查询温度个数", true, false, false));
        commands.add(new CommandData(CommandData.command_type_bat, queryBtAttribute, "查询电池电压/温度", true, false, false));
        commands.add(new CommandData(CommandData.command_type_cycle_count, queryChgCycles, "查询充电循环次数/放电循环次数", true, false, false));
        commands.add(new CommandData(CommandData.command_type_capacity, queryCapacity, "查询单体容量", true, false, false));
        commands.add(new CommandData(CommandData.command_type_alarm, queryAlarms, "查询告警内容指令/保护内容/电池状态/电池", true, false, false));
        commands.add(new CommandData(CommandData.command_type_mos, queryMosStatus, "查询mos开关", true, false, false));
        commands.add(new CommandData(CommandData.command_type_detail_alarm, queryAlarmDetailInfo, "查询单体详细告警", true, false, false));
    }

    /**
     * 发送查询历史数据指令
     *
     * @param count 查询的寄存器数量
     */
    public void sendReadHistoryCommand(int count, String msg, boolean isShowToast) {
        if (isShowToast)
            MyApplication.showToastLoading(CPResourceUtil.getResStringByResId(R.string.reading_params));
        List<String> cmdList = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(count), 2);
        byte[] queryCommand = new byte[]{(byte) 0xfa, 0x46, (byte) 0x07, (byte) 0xCF, InstructionUtil.hexToByte(cmdList.get(0)), InstructionUtil.hexToByte(cmdList.get(1))};//TODO
        commands.offerFirst(new CommandData(CommandData.command_type_read_history_data, queryCommand, msg, true, false, false, isShowToast));
    }

    /**
     * 发送查询指令
     *
     * @param highAddr 寄存器开始地址高
     * @param lowAddr  寄存器开始地址低
     * @param count    查询的寄存器数量
     */
    public void sendReadCommand(int commandType, byte highAddr, byte lowAddr,
                                int count, String msg, boolean isShowToast) {
        if (isShowToast)
            MyApplication.showToastLoading(CPResourceUtil.getResStringByResId(R.string.reading_params));
        List<String> cmdList = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(count), 2);
        byte[] queryCommand = new byte[]{(byte) 0xfa, 0x03, highAddr, lowAddr, InstructionUtil.hexToByte(cmdList.get(0)), InstructionUtil.hexToByte(cmdList.get(1))};//TODO
        commands.offerFirst(new CommandData(commandType, queryCommand, msg, true, false, false, isShowToast));
    }

    /**
     * 发送设置指令
     *
     * @param highAddr 寄存器开始地址高
     * @param lowAddr  寄存器开始地址低
     * @param data     设置的参数
     */
    public void sendWriteCommand(int commandType, byte highAddr, byte lowAddr,
                                 int data, String msg, boolean isShowToast, int parentPosition, int childPosition) {
        if (isShowToast)
            MyApplication.showToastLoading(CPResourceUtil.getResStringByResId(R.string.setting_params));
        List<String> cmdList = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(data), 2);
        byte[] writeCommand = new byte[]{(byte) 0xfa, 0x06, highAddr, lowAddr, InstructionUtil.hexToByte(cmdList.get(0)), InstructionUtil.hexToByte(cmdList.get(1))};
        CommandData commandData = new CommandData(commandType, writeCommand, msg, false, true, false, isShowToast);
        commandData.parentPosition = parentPosition;
        commandData.childPosition = childPosition;
        commands.offerFirst(commandData);
    }

    /**
     * 发送设置指令
     *
     * @param highAddr 寄存器开始地址高
     * @param lowAddr  寄存器开始地址低
     * @param data     设置的参数
     */
    public void sendRestoreCommand(int commandType, byte highAddr, byte lowAddr,
                                   int data, String msg, boolean isShowToast) {
        if (isShowToast)
            MyApplication.showToastLoading(CPResourceUtil.getResStringByResId(R.string.restore_params));
        List<String> cmdList = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(data), 2);
        byte[] writeCommand = new byte[]{(byte) 0xfa, 0x06, highAddr, lowAddr, InstructionUtil.hexToByte(cmdList.get(0)), InstructionUtil.hexToByte(cmdList.get(1))};
        CommandData commandData = new CommandData(commandType, writeCommand, msg, false, true, false, isShowToast);
        commands.offerFirst(commandData);
    }

    public void sendWriteCommand(int commandType, byte highAddr, byte lowAddr, String msg,
                                 boolean isShowToast, int setCount, int... data) {
        if (isShowToast)
            MyApplication.showToastLoading(CPResourceUtil.getResStringByResId(R.string.setting_params));
        List<String> cmdList = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(setCount), 2);
        List<Byte> sendMsg = new ArrayList();
        sendMsg.add((byte) 0xfa);
        sendMsg.add((byte) 0x10);
        sendMsg.add(highAddr);
        sendMsg.add(lowAddr);
        sendMsg.add(InstructionUtil.hexToByte(cmdList.get(0)));
        sendMsg.add(InstructionUtil.hexToByte(cmdList.get(1)));
        sendMsg.add(InstructionUtil.hexToByte(Integer.toHexString(setCount * 2).toUpperCase()));
        for (int item : data) {
            sendMsg.add(InstructionUtil.hexToByte(Integer.toHexString(item).toUpperCase()));
        }
        sendMsg.add((byte) 0xF0);
        sendMsg.add((byte) 0xF0);
        byte[] command = new byte[sendMsg.size()];
        for (int i = 0; i < sendMsg.size(); i++) {
            command[i] = sendMsg.get(i);
        }
        commands.offerFirst(new CommandData(commandType, command, msg, false, true, false, isShowToast));
    }

    /**
     * 发送读取参数指令
     *
     * @param highAddr 寄存器开始地址高
     * @param lowAddr  寄存器开始地址低
     */
    public void sendReadCommand(int commandType, byte highAddr, byte lowAddr,
                                int count, String msg, int parentPosition, int childPosition) {
        List<String> cmdList = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(count), 2);
        byte[] queryCommand = new byte[]{(byte) 0xfa, 0x03, highAddr, lowAddr, InstructionUtil.hexToByte(cmdList.get(0)), InstructionUtil.hexToByte(cmdList.get(1))};//TODO
        CommandData commandData = new CommandData(commandType, queryCommand, msg, true, false, false);
        commandData.parentPosition = parentPosition;
        commandData.childPosition = childPosition;
        commands.offerFirst(commandData);
    }

    /**
     * 发送启动升级指令
     */
    public void sendStartUpgrade() {
        byte[] queryCommand = new byte[]{(byte) 0xAA, 0x55, (byte) 0x80, 0X10, 0x00, 0x00, 0x00, 0x05, (byte) 0x4e, 0x63, (byte) 0xf0, (byte) 0xf0, (byte) 0xe8};//TODO
        CommandData commandData = new CommandData(CommandData.command_type_upgrade_start, queryCommand, "升级消息-下发启动指令", false, false, true);
        commands.offerFirst(commandData);
    }

    /**
     * 发送升级包指令
     */
    public void sendUpgradePackage(String fileName, long fileSize) {
        int fileNameLength = fileName.length();
        byte[] fileSizeBytes = InstructionUtil.numberTo4Bytes(fileSize, true);//文件大小
        byte[] fileNameLengthByte = InstructionUtil.numberTo1Byte(fileNameLength);
        byte[] fileNameBytes = fileName.getBytes();
        int count = fileSizeBytes.length + fileNameBytes.length + fileNameLengthByte.length;
        byte[] countBytes = InstructionUtil.numberTo2Bytes(count, true);
        List<Byte> sendMsg = new ArrayList();
        sendMsg.add((byte) 0xAA);
        sendMsg.add((byte) 0x55);
        sendMsg.add((byte) 0x80);
        sendMsg.add((byte) 0X11);
        sendMsg.add((byte) 0x00);
        sendMsg.add((byte) 0x00);
        addByteToArray(sendMsg, countBytes);
        addByteToArray(sendMsg, fileSizeBytes);
        addByteToArray(sendMsg, fileNameLengthByte);
        addByteToArray(sendMsg, fileNameBytes);
        byte[] command = new byte[sendMsg.size()];
        for (int i = 0; i < sendMsg.size(); i++) {
            command[i] = sendMsg.get(i);
        }
        CommandData commandData = new CommandData(CommandData.command_type_upgrade_package, command, "升级消息-下发升级包指令", false, false, true);
        commands.offerFirst(commandData);
        sendUpgradeCommand();
    }

    private void addByteToArray(List<Byte> sendMsg, byte[] array) {
        for (byte item : array)
            sendMsg.add(item);
    }

    /**
     * 发送升级指令
     */
    public void sendUpgradeMsg(int index, byte[] msg) {
        byte[] indexBytes = InstructionUtil.numberTo2Bytes(index + 1, true);//包序号
        byte[] msgLengthByte = InstructionUtil.numberTo2Bytes(msg.length, true);//消息长度
        List<Byte> sendMsg = new ArrayList();
        sendMsg.add((byte) 0xAA);
        sendMsg.add((byte) 0x55);
        sendMsg.add((byte) 0x80);
        sendMsg.add((byte) 0X12);
        addByteToArray(sendMsg, indexBytes);
        addByteToArray(sendMsg, msgLengthByte);
        addByteToArray(sendMsg, msg);
        byte[] command = new byte[sendMsg.size()];
        for (int i = 0; i < sendMsg.size(); i++) {
            command[i] = sendMsg.get(i);
        }
        CommandData commandData = new CommandData(CommandData.command_type_upgrade_upgrade, command, "升级消息-下发第" + (index + 1) + "包数据升级指令", false, false, true);
        commands.offerFirst(commandData);
        sendUpgradeCommand();
    }

    /**
     * 发送升级指令
     */
    public void sendUpgradeResult(int index) {
        byte[] indexBytes = InstructionUtil.numberTo2Bytes(index + 1, true);//包序号
        List<Byte> sendMsg = new ArrayList();
        sendMsg.add((byte) 0xAA);
        sendMsg.add((byte) 0x55);
        sendMsg.add((byte) 0x80);
        sendMsg.add((byte) 0X13);
        addByteToArray(sendMsg, indexBytes);
        sendMsg.add((byte) 0X00);
        sendMsg.add((byte) 0X00);
        byte[] command = new byte[sendMsg.size()];
        for (int i = 0; i < sendMsg.size(); i++) {
            command[i] = sendMsg.get(i);
        }
        CommandData commandData = new CommandData(CommandData.command_type_upgrade_result, command, "升级消息-下发查询升级结果指令", false, false, true);
        commands.offerFirst(commandData);
        sendUpgradeCommand();
    }

    public static void main(String[] args) {
        List<String> cmdList = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(3), 2);
        List<String> cmdList2 = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(3 * 2), 2);
        List<Byte> sendMsg = new ArrayList();
        sendMsg.add((byte) 0xfa);
        sendMsg.add((byte) 0x10);
        sendMsg.add((byte) 0x00);
        sendMsg.add((byte) 0xc0);
        sendMsg.add(InstructionUtil.hexToByte(cmdList.get(0)));
        sendMsg.add(InstructionUtil.hexToByte(cmdList.get(1)));
        sendMsg.add(InstructionUtil.hexToByte(cmdList2.get(0)));
        sendMsg.add(InstructionUtil.hexToByte(cmdList2.get(1)));
        List<String> cmdList3;
        int[] data = {12, 1110, 1010};
        for (int item : data) {
            cmdList3 = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(item), 2);
            sendMsg.add(InstructionUtil.hexToByte(cmdList3.get(0)));
            sendMsg.add(InstructionUtil.hexToByte(cmdList3.get(1)));
        }
        byte[] command = new byte[sendMsg.size()];
        for (int i = 0; i < sendMsg.size(); i++) {
            command[i] = sendMsg.get(i);
        }

        List<String> list = InstructionUtil.getDivLines(InstructionUtil.intTo2Hex(4 * 2), 1);
        System.out.println(new Gson().toJson(list) + ";" + Integer.toHexString(8).toUpperCase());
        int a = 12;
        System.out.println(ByteUtils.bytesToHexString(InstructionUtil.numberTo1Byte(12)));
        byte[] queryCommand = new byte[]{(byte) 0xAA, 0x55, (byte) 0x80, 0X10, 0x00, 0x00, 0x00, 0x05, (byte) 0x4e, 0x63, (byte) 0xf0, (byte) 0xf0, (byte) 0xe8};//TODO
        byte[] newCommand = InstructionUtil.packageInstruction(queryCommand, true);
        for (byte item : newCommand) {
            System.out.println("item:" + InstructionUtil.byte2Hex(item));
        }
        newCommand = InstructionUtil.packageInstruction(queryCommand, false);
        for (byte item : newCommand) {
            System.out.println("item:" + InstructionUtil.byte2Hex(item));
        }


    }


}
