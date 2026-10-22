package com.huasu.bess4c48.event;


import com.huasu.bess4c48.bean.BTGRealtimeData;

/**
 * Event管理类
 */
public class EventManager {

    /**
     * 收到电池组数据
     */
    public static class OnShowBtgRealtimeDataEvent {
        private BTGRealtimeData mBTGRealtimeData;

        public OnShowBtgRealtimeDataEvent(BTGRealtimeData btgRealtimeData) {
            this.mBTGRealtimeData = btgRealtimeData;
        }

        public BTGRealtimeData getBTGRealtimeData() {
            return mBTGRealtimeData;
        }
    }

    /**
     * 蓝牙连接成功
     */
    public static class OnBleConnectSuccessEvent {

        public OnBleConnectSuccessEvent() {

        }
    }

    /**
     * 蓝牙连接失败
     */
    public static class OnBleConnectFailedEvent {

        public OnBleConnectFailedEvent() {

        }
    }


    /**
     * 蓝牙断开事件
     */
    public static class OnBleDisconnectEvent {
        public OnBleDisconnectEvent() {

        }
    }


    /**
     * 页面切换
     */
    public static class OnFragmentSwitchEvent {

        private int mFragmentPosition;

        public OnFragmentSwitchEvent(int position) {
            this.mFragmentPosition = position;
        }

        public int getPosition() {
            return mFragmentPosition;
        }
    }

    /**
     * 手机跟保护板通讯异常
     */
    public static class OnConnectFailEvent {

        public OnConnectFailEvent() {

        }
    }

    /**
     * 收到参数
     */
    public static class OnShowDeviceParamEvent {
        private int commandType;
        private byte[] receiveArray;
        private boolean showToast;

        public OnShowDeviceParamEvent(int commandType, byte[] receiveArray, boolean showToast) {
            this.commandType = commandType;
            this.receiveArray = receiveArray;
            this.showToast = showToast;
        }

        public int getCommandType() {
            return commandType;
        }

        public byte[] getReceiveArray() {
            return receiveArray;
        }

        public boolean isShowToast() {
            return showToast;
        }
    }

    /**
     * 收到写入参数回调
     */
    public static class OnShowSettingDeviceParamEvent {
        private boolean showToast, isSuccess;
        private int parentPosition, childPosition;

        public OnShowSettingDeviceParamEvent(boolean isSuccess, boolean showToast, int parentPosition, int childPosition) {
            this.showToast = showToast;
            this.childPosition = childPosition;
            this.parentPosition = parentPosition;
            this.isSuccess = isSuccess;
        }

        public boolean isShowToast() {
            return showToast;
        }

        public int getParentPosition() {
            return parentPosition;
        }

        public int getChildPosition() {
            return childPosition;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }

    /**
     * 收到恢复参数回调
     */
    public static class OnRestoreDeviceParamEvent {
        private boolean showToast, isSuccess;

        public OnRestoreDeviceParamEvent(boolean isSuccess, boolean showToast) {
            this.showToast = showToast;
            this.isSuccess = isSuccess;
        }

        public boolean isShowToast() {
            return showToast;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }

    /**
     * 收到读取参数回调
     */
    public static class OnShowReadOneDeviceParamEvent {
        private int parentPosition, childPosition;
        private byte[] receiveArray;

        public OnShowReadOneDeviceParamEvent(byte[] receiveArray, int parentPosition, int childPosition) {
            this.childPosition = childPosition;
            this.parentPosition = parentPosition;
            this.receiveArray = receiveArray;
        }

        public int getParentPosition() {
            return parentPosition;
        }

        public int getChildPosition() {
            return childPosition;
        }

        public byte[] getReceiveArray() {
            return receiveArray;
        }
    }

    /**
     * 收到读取历史数据回调
     */
    public static class OnShowHistoryDataEvent {
        private byte[] receiveArray;
        private boolean isCrcResult;

        public OnShowHistoryDataEvent(byte[] receiveArray, boolean isCrcResult) {
            this.receiveArray = receiveArray;
            this.isCrcResult = isCrcResult;
        }

        public byte[] getReceiveArray() {
            return receiveArray;
        }

        public boolean isCrcResult() {
            return isCrcResult;
        }
    }

    /**
     * 收到读取历史数据回调
     */
    public static class OnShowHistoryDataFailEvent {

        public OnShowHistoryDataFailEvent() {
        }

    }

    //发送升级失败消息
    public static class OnSendUpgradeFail {
        private int reason;
        public OnSendUpgradeFail(int reason) {
            this.reason=reason;
        }

        public int getReason() {
            return reason;
        }
    }

    //发送升级包消息
    public static class OnSendUpgradePackageStart {
        public OnSendUpgradePackageStart() {
        }
    }

    //发送开始升级消息
    public static class OnSendStartUpgrade {
        //1：128Byte；
        //2：256Byte；
        //3：512Byte
        private int word;

        public OnSendStartUpgrade(int word) {
            this.word = word;
        }

        public int getWord() {
            return word;
        }
    }


    //收到升级过程消息
    public static class OnReceiveUpgradeProcessMsg {
        private int msgCode;

        //0、终端校验升级包通过回复继续下发；
        //1、校验不通过回复重发指令；
        //2、超过失败次数，回复停止下载；其他保留。
        public OnReceiveUpgradeProcessMsg(int msgCode) {
            this.msgCode = msgCode;
        }

        public int getMsgCode() {
            return msgCode;
        }
    }


    //发送升级结果消息
    public static class OnReceiveUpgradeResult {
        private boolean result;

        //0、终端校验升级包通过回复继续下发；
        //1、校验不通过回复重发指令；
        //2、超过失败次数，回复停止下载；其他保留。
        public OnReceiveUpgradeResult(boolean result) {
            this.result = result;
        }

        public boolean isResult() {
            return result;
        }
    }
}