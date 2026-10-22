package com.huasu.bess4c48.callback;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.app.hubert.guide.util.LogUtil;
import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.BleConstant;
import com.huasu.bess4c48.utils.BleHelper;
import com.huasu.bess4c48.utils.ByteUtils;

import org.greenrobot.eventbus.EventBus;

public class BleCallback extends BluetoothGattCallback {
    public final String TAG = BleCallback.class.getSimpleName();
    private UiCallback uiCallback;

    public BleCallback(UiCallback uiCallback) {
        this.uiCallback = uiCallback;
    }

    /**
     * 连接状态回调
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Log.e("SYL", "onConnectionStateChange" + " " + status + ";" + newState
                + ";gatt" + gatt);
        MyApplication.isConnected = false;


        if (status == BluetoothGatt.GATT_SUCCESS) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    if (uiCallback != null)
                        uiCallback.state("-1");
                    //获取MtuSize,会触发onMtuChanged回调
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gatt.requestMtu(517);
                    EventBus.getDefault().post(new EventManager.OnBleConnectSuccessEvent());
                    Log.e("SYL", "onConnectionStateChange:连接成功，" + gatt.getDevice().getName() + ";");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("SYL", "被动断开，准备重连");
                    break;
                default:
                    EventBus.getDefault().post(new EventManager.OnBleConnectFailedEvent());
                    break;
            }
        } else {
            gatt.connect();
        }
    }

    /**
     * Mtu改变回调
     *
     * @param gatt   gatt
     * @param mtu    new MTU size
     * @param status gatt状态
     */
    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        Log.e(TAG, "onMtuChanged:mtu: " + mtu + " status: " + status);
        //发现服务
        gatt.discoverServices();
    }

    /**
     * 发现服务回调
     *
     * @param gatt
     * @param status gatt状态
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.e(TAG, "onServicesDiscovered");
        boolean notifyOpen = BleHelper.getInstance().enableIndicateNotification();
        //开启通知失败时断开gatt连接。开启成功后会进入onDescriptorWrite回调
        if (!notifyOpen) {
            Log.e(TAG, "开启通知属性异常");
            gatt.disconnect();
            gatt.close();
        } else {

            Log.e(TAG, "onServicesDiscovered：开启通知属性正常");
            //通知打开成功后发送一条请求地址消息
//            BleHelper.getInstance().sendCommand("ADDR");
        }
    }

    /**
     * 描述符写入回调
     */
    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.e(TAG, "onDescriptorWrite");
        if (BleConstant.DESCRIPTOR_UUID.equals(descriptor.getUuid().toString().toLowerCase())) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onDescriptorWrite：通知开启成功");
                //获取phy,Android版本必须要在8.0和8.0以上，因此如果你的Android设备是低版本的就不用考虑去使用这个API了，因为系统不支持
                //调用这个方法API就会触发onPhyRead回调。我们在回调的时候打印一下内容，看看当前的硬件Phy是什么值。
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    gatt.readPhy();
                }
                //读取描述符，会触发onDescriptorRead回调
                gatt.readDescriptor(descriptor);
                //读取RSSI，会触发onReadRemoteRssi回调
                gatt.readRemoteRssi();


            } else {
                Log.e(TAG, "onDescriptorWrite：通知开启失败");
            }
        }
    }

    /**
     * 读取物理层回调
     *
     * @param gatt
     * @param txPhy  发送速率   1M  2M
     * @param rxPhy  接收速率   1M  2M
     * @param status 更新操作的状态
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        Log.e(TAG, "onPhyRead：txPhy： " + txPhy + " rxPhy: " + rxPhy);
        if (txPhy == BluetoothDevice.PHY_LE_1M && rxPhy == BluetoothDevice.PHY_LE_1M) {
            //设置2M的发送速率和接收速率，这里设置完之后，会触发onPhyUpdate回调
            gatt.setPreferredPhy(BluetoothDevice.PHY_LE_2M, BluetoothDevice.PHY_LE_2M, BluetoothDevice.PHY_OPTION_NO_PREFERRED);
        }
    }

    /**
     * 物理层改变回调
     *
     * @param gatt
     * @param txPhy  发送速率   1M  2M
     * @param rxPhy  接收速率   1M  2M
     * @param status 更新操作的状态
     */
    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        Log.e(TAG, "onPhyUpdate：txPhy： " + txPhy + " rxPhy: " + rxPhy);
    }

    /**
     * 特性读取回调
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.e(TAG, "onCharacteristicRead：characteristic: " + characteristic.getUuid().toString());
    }

    /**
     * 特性写入回调
     */
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        String command = ByteUtils.bytesToHexString(characteristic.getValue());
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.e(TAG, "onCharacteristicWrite：写入成功: " + command);
        } else {
            Log.e(TAG, "onCharacteristicWrite：写入失败: " + command);
        }
        //读取特性，会触发onCharacteristicRead回调
        gatt.readCharacteristic(characteristic);
    }

    /**
     * 特性改变回调
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String content = ByteUtils.bytesToHexString(characteristic.getValue());
        Log.e(TAG, "收到内容onCharacteristicChanged时间: " + System.currentTimeMillis());
        Log.e(TAG, "onCharacteristicChanged：收到内容: " + content + ";" + characteristic.getValue().length);
        //读取特性，会触发onCharacteristicRead回调
//        Log.e(TAG, "onCharacteristicChanged：读取属性: " + gatt.readCharacteristic(characteristic));
        if (uiCallback != null) {
            uiCallback.showReceiveData(characteristic.getValue());
        }

    }


    /**
     * 读取远程设备的信号强度回调
     *
     * @param gatt
     * @param rssi   信号强度
     * @param status
     */
    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        Log.e(TAG, "onReadRemoteRssi：rssi: " + rssi);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.e(TAG, "onDescriptorRead：descriptor: " + descriptor.getUuid().toString());
        if (uiCallback != null)
            uiCallback.connectSuccess();
        MyApplication.isConnected = true;
    }
}

