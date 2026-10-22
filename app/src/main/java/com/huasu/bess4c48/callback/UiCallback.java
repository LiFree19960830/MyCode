package com.huasu.bess4c48.callback;

public interface UiCallback {
    /**
     * 当前Ble状态信息
     */
    void state(String state);

    //连接蓝牙失败，重新连接
    void connectFail();

    //连接蓝牙成功
    void connectSuccess();

    //收到数据
    void showReceiveData(byte[] receiveData);
}
