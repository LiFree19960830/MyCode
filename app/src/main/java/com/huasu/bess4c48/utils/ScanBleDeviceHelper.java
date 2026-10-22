package com.huasu.bess4c48.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;

import com.huasu.bess4c48.bean.MyDevice;
import com.huasu.bess4c48.callback.ScanBleDeviceCallback;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BLUETOOTH_SERVICE;

public class ScanBleDeviceHelper {

    private static final String TAG = ScanBleDeviceHelper.class.getSimpleName();
    //获取系统蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //扫描者
    private BluetoothLeScanner mScanner;
    private ActivityResultLauncher<Intent> enableBluetooth;         //打开蓝牙意图
    private ActivityResultLauncher<String> requestBluetoothConnect;//请求蓝牙连接权限意图
    private ActivityResultLauncher<String> requestBluetoothScan;//请求蓝牙扫描权限意图
    private ActivityResultLauncher<String> requestLocation;//请求定位权限（Android6.0-Android12）
    private ComponentActivity mAct;
    //是否正在扫描
    private boolean isScanning;
    private ScanBleDeviceCallback mScanBleDeviceCallback;
    private List<MyDevice> myDeviceList;
    private String address;//蓝牙地址
    private int searchType = 1; //1代表扫描全部蓝牙  2表示根据蓝牙地址获取蓝牙


    //获取单例
    public ScanBleDeviceHelper(ComponentActivity act, ScanBleDeviceCallback scanBleDeviceCallback) {
        mAct = act;
        mScanBleDeviceCallback = scanBleDeviceCallback;
        registerIntent();
    }

    private void registerIntent() {
        //打开蓝牙意图
        enableBluetooth = mAct.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (isOpenBluetooth()) {
                        if (isAndroid12()) {
                            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                                requestBluetoothConnect.launch(Manifest.permission.BLUETOOTH_CONNECT);
                                return;
                            }
                            if (hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                                if (searchType == 1) {
                                    //扫描或者停止扫描
                                    if (isScanning) stopScan();
                                    else startScan();
                                } else {
                                    if (mScanBleDeviceCallback != null && !TextUtils.isEmpty(address))
                                        mScanBleDeviceCallback.showRemoteDevice(mBluetoothAdapter.getRemoteDevice(address));
                                }
                            } else {
                                //请求权限
                                requestBluetoothScan.launch(Manifest.permission.BLUETOOTH_SCAN);
                            }
                        } else {
                            if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                if (searchType == 1) {
                                    //扫描或者停止扫描
                                    if (isScanning) stopScan();
                                    else startScan();
                                } else {
                                    if (mScanBleDeviceCallback != null && !TextUtils.isEmpty(address))
                                        mScanBleDeviceCallback.showRemoteDevice(mBluetoothAdapter.getRemoteDevice(address));
                                }
                            } else {
                                requestLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                            }
                        }
                    } else {
                        if (mScanBleDeviceCallback != null)
                            mScanBleDeviceCallback.showError(2);
                    }
                } else {
                    //获取系统权限被拒绝的回调
                    if (mScanBleDeviceCallback != null) {
                        mScanBleDeviceCallback.requestPermissionFailed();
                    }
                }
            }
        });
        //请求BLUETOOTH_CONNECT权限意图
        requestBluetoothConnect = mAct.registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result)
                    enableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                else if (mScanBleDeviceCallback != null) {
                    // ToastUtils.show("Android12中未获取此权限，无法打开蓝牙");
                    mScanBleDeviceCallback.showError(3);
                    mScanBleDeviceCallback.requestPermissionFailed();
                }
            }
        });
        //请求BLUETOOTH_SCAN权限意图
        requestBluetoothScan = mAct.registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    if (searchType == 1) {
                        //扫描
                        startScan();
                    } else {
                        if (mScanBleDeviceCallback != null && !TextUtils.isEmpty(address))
                            mScanBleDeviceCallback.showRemoteDevice(mBluetoothAdapter.getRemoteDevice(address));
                    }
                } else {
//                    ToastUtils.show("Android12中未获此权限，则无法扫描蓝牙");
                    if (mScanBleDeviceCallback != null) {
                        mScanBleDeviceCallback.showError(4);
                        mScanBleDeviceCallback.requestPermissionFailed();
                    }
                }

            }
        });
        //请求定位权限
        requestLocation = mAct.registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    if (searchType == 1) {
                        //扫描
                        startScan();
                    } else {
                        if (mScanBleDeviceCallback != null && !TextUtils.isEmpty(address))
                            mScanBleDeviceCallback.showRemoteDevice(mBluetoothAdapter.getRemoteDevice(address));
                    }
                } else {
//                    ToastUtils.show("Android12以下，Android6及以上需要定位权限才能扫描设备");
                    if (mScanBleDeviceCallback != null) {
                        mScanBleDeviceCallback.showError(1);
                        mScanBleDeviceCallback.requestPermissionFailed();
                    }
                }
            }
        });
    }

    private void startScan() {
        if (!isScanning) {
            mScanner.startScan(scanCallback);
            isScanning = true;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, 5000);
    }

    private void stopScan() {
        try {
            if (isScanning) {
                mScanner.stopScan(scanCallback);
                isScanning = false;
                Log.e(TAG, "停止扫描");
                if (mScanBleDeviceCallback != null)
                    mScanBleDeviceCallback.showAllDevice(myDeviceList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scanBleDevice() {
        if (!isOpenBluetooth()) {
            //是Android12
            if (isAndroid12()) {
                //检查是否有BLUETOOTH_CONNECT权限
                if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                    //打开蓝牙
                    enableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                } else {
                    //请求权限
                    requestBluetoothConnect.launch(Manifest.permission.BLUETOOTH_CONNECT);
                }
                return;
            }
            //不是Android12 直接打开蓝牙
            enableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        } else {
            if (isAndroid12()) {
                if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                    requestBluetoothConnect.launch(Manifest.permission.BLUETOOTH_CONNECT);
                    return;
                }
                if (hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                    //扫描或者停止扫描
                    if (isScanning) stopScan();
                    else startScan();
                } else {
                    //请求权限
                    requestBluetoothScan.launch(Manifest.permission.BLUETOOTH_SCAN);
                }
            } else {
                if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //扫描或者停止扫描
                    if (isScanning) stopScan();
                    else startScan();
                } else {
                    requestLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
        }
    }

    /**
     * 判断蓝牙是否打开
     *
     * @return
     */
    private boolean isOpenBluetooth() {
        BluetoothManager manager = (BluetoothManager) mAct.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        return mBluetoothAdapter.isEnabled();
    }

    public void getRemoteDeviceByAddress(String address) {
        if (TextUtils.isEmpty(address))
            return;
        this.address = address;
        searchType = 2;
        if (isOpenBluetooth()) {
            if (mScanBleDeviceCallback != null)
                mScanBleDeviceCallback.showRemoteDevice(mBluetoothAdapter.getRemoteDevice(address));
            return;
        }
        //是Android12
        if (isAndroid12()) {
            //检查是否有BLUETOOTH_CONNECT权限
            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                //打开蓝牙
                enableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            } else {
                //请求权限
                requestBluetoothConnect.launch(Manifest.permission.BLUETOOTH_CONNECT);
            }
            return;
        }
        //不是Android12 直接打开蓝牙
        enableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
    }


    private boolean isAndroid12() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String permission) {
        return mAct.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    //扫描结果回调
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (TextUtils.isEmpty(device.getName()))
                if (myDeviceList == null)
                    myDeviceList = new ArrayList<>();
            MyDevice myDevice = new MyDevice(device, result.getRssi());
            addDevice(myDevice);
            Log.e(TAG, "name: " + device.getName() + ", address: " + device.getAddress());
            if (mScanBleDeviceCallback != null)
                mScanBleDeviceCallback.showDevice(myDevice);
        }
    };


    private void addDevice(MyDevice scanDevice) {
        try {
            boolean exist = false;
            if (myDeviceList != null && !myDeviceList.isEmpty()) {
                for (MyDevice item : myDeviceList) {
                    if (item.getDevice().getAddress().equals(scanDevice.getDevice().getAddress())) {
                        item.setRssi(scanDevice.getRssi());
                        exist = true;
                        break;
                    }
                }
            }
            if (!exist) {
                myDeviceList.add(scanDevice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
