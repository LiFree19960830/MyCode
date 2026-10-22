package com.huasu.bess4c48.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.callback.BleCallback;
import com.huasu.bess4c48.callback.MyUiCallback;
import com.huasu.bess4c48.event.EventManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;

public class BleHelper {
    public static final String TAG = BleHelper.class.getSimpleName();

    private BluetoothGatt mBleGatt;
    private BluetoothGattService mBleGattService;
    private BluetoothGattCharacteristic mBleGattCharacteristic;

    private static MyUiCallback uiCallback;
    private static BleCallback mBleCallback;
    ;
    private static Context mCxt;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;


    private static class SingletonHolder {
        private static final BleHelper INSTANCE = new BleHelper();
    }

    //获取单例
    public static BleHelper getInstance() {
        mCxt = MyApplication.mApplication;
        uiCallback = MyUiCallback.getInstance();
        mBleCallback = new BleCallback(uiCallback);
        return SingletonHolder.INSTANCE;
    }

    /**
     * 断开蓝牙
     */
    public void disconnectOtherGatt() {
        Log.e("SYL", "disconnectOtherGatt:");
        if (mBleGatt != null) {
            mBleGatt.disconnect();
            mBleGatt.close();
            mBleGatt = null;
        }
        mBluetoothDevice = null;
    }


    /**
     * 连接蓝牙
     *
     * @param bleDevice
     */
    public void connectGatt(BluetoothDevice bleDevice) {
        Log.e("SYL", "开始连接蓝牙");
        if (bleDevice == null)
            return;
        mBluetoothDevice = bleDevice;
        if (isOpenBluetooth()) {
            if (mBleGatt != null) {
                mBleGatt.close();
                uiCallback.state("-1");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBleGatt = bleDevice.connectGatt(mCxt, true, mBleCallback, BluetoothDevice.TRANSPORT_LE);
            } else {
                mBleGatt = bleDevice.connectGatt(mCxt, true, mBleCallback);
            }
            MyApplication.showLoadingDialog(MyApplication.getActivity(), CPResourceUtil.getResStringByResId(R.string.bluetooth_connecting), 15000);
        } else {
            MyApplication.showFailedDialog(MyApplication.getActivity(), CPResourceUtil.getResStringByResId(R.string.toast_please_open_bluetooth));
        }
    }


    /**
     * 判断蓝牙是否打开
     *
     * @return
     */
    private boolean isOpenBluetooth() {
        BluetoothManager manager = (BluetoothManager) MyApplication.getInstance().getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 断开蓝牙
     */
    public void disconnectGatt() {
        if (mBleGatt != null) {
            mBleGatt.disconnect();
            mBleGatt.close();
            EventBus.getDefault().post(new EventManager.OnBleDisconnectEvent());
        }
    }


    /**
     * 启用指令通知
     */
    public boolean enableIndicateNotification() {
        //获取Gatt 服务
        mBleGattService = mBleGatt.getService(UUID.fromString(BleConstant.OTA_SERVICE_UUID));
        if (mBleGattService == null) {
            return false;
        }

        mBleGattCharacteristic = mBleGattService.getCharacteristic(UUID.fromString(BleConstant.OTA_CHARACTERISTIC_WRITE_UUID));
        //获取Gatt 特征（特性）
        BluetoothGattCharacteristic gattCharacteristic = mBleGattService.getCharacteristic(UUID.fromString(BleConstant.OTA_CHARACTERISTIC_INDICATE_UUID));
        return setCharacteristicNotification(gattCharacteristic);
    }

    /**
     * 设置特征通知
     * return true, if the write operation was initiated successfully
     */
    private boolean setCharacteristicNotification(BluetoothGattCharacteristic gattCharacteristic) {
        //如果特性具备Notification功能，返回true就代表设备设置成功
        boolean isEnableNotification = mBleGatt.setCharacteristicNotification(gattCharacteristic, true);
        if (isEnableNotification) {
            //构建BluetoothGattDescriptor对象
            BluetoothGattDescriptor gattDescriptor = gattCharacteristic.getDescriptor(UUID.fromString(BleConstant.DESCRIPTOR_UUID));
            if (gattDescriptor == null) {
                List<BluetoothGattDescriptor> gattDescriptorList = gattCharacteristic.getDescriptors();
                if (gattDescriptorList != null && !gattDescriptorList.isEmpty()) {
                    for (BluetoothGattDescriptor item : gattDescriptorList) {
                        Log.e(TAG, " BluetoothGattDescriptor uuid: " + item.getUuid().toString());
                        gattDescriptor = item;
                        BleConstant.DESCRIPTOR_UUID = item.getUuid().toString();
                        break;
                    }
                }
            }
            gattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            //写入描述符
            return mBleGatt.writeDescriptor(gattDescriptor);
        } else {
            return false;
        }
    }

    public void sendCommand(byte[] command, boolean reverse) {
        try {
            Log.e(TAG, "----------query---command 为:" + Arrays.toString(command) + "----------");
            byte[] queryCMD = InstructionUtil.packageInstruction(command, reverse);
            Log.e(TAG, "----------query--CMD为:" + Arrays.toString(queryCMD) + "----------");
            Log.e(TAG, "---------- 开始查询数据----------");
            if (mBleGattCharacteristic == null)
                mBleGattCharacteristic = mBleGattService.getCharacteristic(UUID.fromString(BleConstant.OTA_CHARACTERISTIC_WRITE_UUID));
            if (mBleGattCharacteristic == null) {
                List<BluetoothGattCharacteristic> characteristicList = mBleGattService.getCharacteristics();
                if (characteristicList != null && !characteristicList.isEmpty()) {
                    for (BluetoothGattCharacteristic item : characteristicList) {
                        Log.e(TAG, " BluetoothGattDescriptor uuid: " + item.getUuid().toString());
                        mBleGattCharacteristic = item;
                        BleConstant.OTA_CHARACTERISTIC_WRITE_UUID = item.getUuid().toString();
                        break;
                    }
                }
            }
            if (mBleGattCharacteristic == null) {
                Log.e(TAG, "sendCommand: 特性未找到");
                return;
            }
            //写入类型  WRITE_TYPE_DEFAULT  默认有响应， WRITE_TYPE_NO_RESPONSE  无响应。
            mBleGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            //将字符串command转Byte后进行写入
            mBleGattCharacteristic.setValue(queryCMD);
            boolean result = mBleGatt.writeCharacteristic(mBleGattCharacteristic);
            Log.d(TAG, result ? "写入初始化成功：" + Arrays.toString(queryCMD) : "写入初始化失败：" + Arrays.toString(queryCMD));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendCommand(byte[] command, String msg, boolean reverse) {
        try {
            Log.e(TAG, "----------query---commandStr 为:" + msg + " " + reverse + "；command 为:" + ByteUtils.bytesToHexString(command) + "----------");
            byte[] queryCMD = InstructionUtil.packageInstruction(command, reverse);
            Log.e(TAG, "----------query--CMD为:" + msg + ByteUtils.bytesToHexString(queryCMD) + "----------");
            Log.e(TAG, "---------- 开始查询数据----------");
            if (mBleGattCharacteristic == null)
                mBleGattCharacteristic = mBleGattService.getCharacteristic(UUID.fromString(BleConstant.OTA_CHARACTERISTIC_WRITE_UUID));
            if (mBleGattCharacteristic == null) {
                List<BluetoothGattCharacteristic> characteristicList = mBleGattService.getCharacteristics();
                if (characteristicList != null && !characteristicList.isEmpty()) {
                    for (BluetoothGattCharacteristic item : characteristicList) {
                        Log.e(TAG, " BluetoothGattDescriptor uuid: " + item.getUuid().toString());
                        mBleGattCharacteristic = item;
                        BleConstant.OTA_CHARACTERISTIC_WRITE_UUID = item.getUuid().toString();
                        break;
                    }
                }
            }
            if (mBleGattCharacteristic == null) {
                Log.e(TAG, "sendCommand: 特性未找到");
                return;
            }
            //写入类型  WRITE_TYPE_DEFAULT  默认有响应， WRITE_TYPE_NO_RESPONSE  无响应。
            mBleGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            //将字符串command转Byte后进行写入
            mBleGattCharacteristic.setValue(queryCMD);
            boolean result = mBleGatt.writeCharacteristic(mBleGattCharacteristic);
            Log.d(TAG, result ? "写入初始化成功：" + Arrays.toString(queryCMD) : "写入初始化失败：" + Arrays.toString(queryCMD));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendCommand1(byte[] command) {
        try {
            if (mBleGattCharacteristic == null)
                mBleGattCharacteristic = mBleGattService.getCharacteristic(UUID.fromString(BleConstant.OTA_CHARACTERISTIC_WRITE_UUID));
            if (mBleGattCharacteristic == null) {
                List<BluetoothGattCharacteristic> characteristicList = mBleGattService.getCharacteristics();
                if (characteristicList != null && !characteristicList.isEmpty()) {
                    for (BluetoothGattCharacteristic item : characteristicList) {
                        Log.e(TAG, " BluetoothGattDescriptor uuid: " + item.getUuid().toString());
                        mBleGattCharacteristic = item;
                        BleConstant.OTA_CHARACTERISTIC_WRITE_UUID = item.getUuid().toString();
                        break;
                    }
                }
            }
            if (mBleGattCharacteristic == null) {
                Log.e(TAG, "sendCommand: 特性未找到");
                return;
            }
            //写入类型  WRITE_TYPE_DEFAULT  默认有响应， WRITE_TYPE_NO_RESPONSE  无响应。
            mBleGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            //将字符串command转Byte后进行写入
            mBleGattCharacteristic.setValue(command);
            boolean result = mBleGatt.writeCharacteristic(mBleGattCharacteristic);
            Log.d(TAG, result ? "写入初始化成功：" + Arrays.toString(command) : "写入初始化失败：" + Arrays.toString(command));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendCommand(String msg) {
        try {
            if (mBleGattCharacteristic == null)
                mBleGattCharacteristic = mBleGattService.getCharacteristic(UUID.fromString(BleConstant.OTA_CHARACTERISTIC_WRITE_UUID));
            if (mBleGattCharacteristic == null) {
                Log.e(TAG, "sendCommand: 特性未找到");
                return;
            }
            //写入类型  WRITE_TYPE_DEFAULT  默认有响应， WRITE_TYPE_NO_RESPONSE  无响应。
            mBleGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            //将字符串command转Byte后进行写入
            byte[] bytes = hexStringToBytes(msg);
            mBleGattCharacteristic.setValue(bytes);

            Log.e(TAG, "onConnectionStateChange:连接成功;发送数据" + mBleGatt);
            boolean result = mBleGatt.writeCharacteristic(mBleGattCharacteristic);
            Log.d(TAG, result ? "写入初始化成功：" + msg : "写入初始化失败：" + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private byte[] hexStringToBytes(@NonNull String var0) {
        if (var0 != null) {
            char[] var5 = new char[1000];
            char[] var1 = var0.toCharArray();
            int var2 = 0;

            for (int var3 = 0; var3 < var1.length; ++var3) {
                if (var1[var3] != ' ') {
                    var5[var2] = var1[var3];
                    ++var2;
                }
            }

            int var7;
            if (var2 % 2 == 0) {
                var7 = var2;
            } else {
                var7 = var2 + 1;
            }

            if (var7 != 0) {
                int[] var9;
                (var9 = new int[var7])[var7 - 1] = 0;

                int var4;
                for (var4 = 0; var4 < var2; ++var4) {
                    if (var5[var4] >= '0' && var5[var4] <= '9') {
                        var9[var4] = var5[var4] - 48;
                    } else if (var5[var4] >= 'a' && var5[var4] <= 'f') {
                        var9[var4] = var5[var4] - 97 + 10;
                    } else if (var5[var4] >= 'A' && var5[var4] <= 'F') {
                        var9[var4] = var5[var4] - 65 + 10;
                    }
                }

                int var6;
                byte[] var8 = new byte[var6 = var7 / 2];

                for (var2 = 0; var2 < var6; ++var2) {
                    var8[var2] = (byte) (var9[var4 = var2 * 2] * 16 + var9[var4 + 1]);
                }

                return var8;
            }
        }

        return new byte[0];
    }

    public BluetoothDevice getCurrentBluetoothDevice() {
        if (mBleGatt == null)
            return null;
        return mBleGatt.getDevice();
    }


    public BluetoothGatt getCurrentBluetoothGatt() {
        return mBleGatt;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }
}
