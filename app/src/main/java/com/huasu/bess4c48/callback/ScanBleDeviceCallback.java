package com.huasu.bess4c48.callback;

import android.bluetooth.BluetoothDevice;

import com.huasu.bess4c48.bean.MyDevice;

import java.util.List;

public interface ScanBleDeviceCallback {
    /**
     * 当前Ble状态信息
     */
    void showDevice(MyDevice device);

    //显示失败信息

    /**
     * 1:Android12以下，Android6及以上需要定位权限才能扫描设备
     * 2:蓝牙未打开
     * 3:Android12中未获取此权限，无法打开蓝牙
     * 4:Android12中未获此权限，则无法扫描蓝牙
     * @param code
     */
    void showError(int code);

    void showAllDevice(List<MyDevice> deviceList);

    //获取蓝牙对象
    void showRemoteDevice(BluetoothDevice device);

    // 获取系统权限被拒绝的回调
    void requestPermissionFailed();

}
