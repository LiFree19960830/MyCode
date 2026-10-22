package com.huasu.bess4c48.bean;

import android.bluetooth.BluetoothDevice;

public class MyDevice {
    private BluetoothDevice device;
    private int rssi; //手机与蓝牙设备之间的新号强度

    private String updateName;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public MyDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }


    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }
}
