package com.huasu.bess4c48.ui.sysSetting.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.adapter.BluetoothHistoryListAdapter;
import com.huasu.bess4c48.adapter.BluetoothScanListAdapter;
import com.huasu.bess4c48.base.BaseActivity;
import com.huasu.bess4c48.bean.MyBleDevice;
import com.huasu.bess4c48.bean.MyDevice;
import com.huasu.bess4c48.callback.ScanBleDeviceCallback;
import com.huasu.bess4c48.database.MyDatabase;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.BleHelper;
import com.huasu.bess4c48.utils.ScanBleDeviceHelper;
import com.huasu.bess4c48.view.TableNoData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BluetoothManageActivity extends BaseActivity {
    private TextView tvCurrentDeviceName;
    private TextView tvScanBtn;
    private RecyclerView rvBleScanList;

    private RecyclerView rvHistoryDevice;

    private ScanBleDeviceHelper scanBleDeviceHelper;

    private BluetoothScanListAdapter bluetoothScanListAdapter;

    private BluetoothHistoryListAdapter bluetoothHistoryListAdapter;

    private TableNoData tableNoDataBleScanList;

    private TableNoData tableNoDataHistoryDevice;


    private List<MyBleDevice> myBleDeviceList = new ArrayList<>();
    private BluetoothDevice mConnectDevice;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_system_setting;
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        mConnectDevice = BleHelper.getInstance().getCurrentBluetoothDevice();
        initScanBleDeviceHelper();
        initCurrentDevice();
        initHistoryBleDeviceList();
        initScanBleDeviceList();
        initScanBtn();
        queryHisotryBluetoothDeviceList();
    }

    /**
     * 渲染当前设备 / 断开功能
     */
    public void initCurrentDevice() {
        tvCurrentDeviceName = findViewById(R.id.tvCurrentDeviceName);
        if (mConnectDevice != null && MyApplication.getInstance().isConnected){
           List<MyBleDevice> devices = MyDatabase.getDatabase().getMyBleDeviceDao().queryByAddress(mConnectDevice.getAddress());
           if(devices!=null &&!devices.isEmpty())
               tvCurrentDeviceName.setText(devices.get(0).getBleName());
           else
               tvCurrentDeviceName.setText(mConnectDevice.getName());
        }

    }


    /**
     * 渲染已连接过的蓝牙列表
     */
    public void initHistoryBleDeviceList() {

        tableNoDataHistoryDevice = findViewById(R.id.tableNoDataHistoryDevice);
        rvHistoryDevice = findViewById(R.id.rvHistoryDevice);

        LinearLayoutManager layoutManagerHistory = new LinearLayoutManager(mContext);
        bluetoothHistoryListAdapter = new BluetoothHistoryListAdapter();
        rvHistoryDevice.setLayoutManager(layoutManagerHistory);
        rvHistoryDevice.setAdapter(bluetoothHistoryListAdapter);

        bluetoothHistoryListAdapter.setListener(new BluetoothHistoryListAdapter.onClickListener() {
            @Override
            public void onConnectBtnClick(MyBleDevice device) {
                scanBleDeviceHelper.getRemoteDeviceByAddress(device.bleAddress);
//                if (!MyApplication.isConnected) {
//                    scanBleDeviceHelper.getRemoteDeviceByAddress(device.bleAddress);
//                } else {
//                    boolean sameAddress = device.bleAddress.equals(mConnectDevice.getAddress());
//                    if (MyApplication.isConnected&&sameAddress) {
//                        MyApplication.showSuccessDialog(getActivity(), getString(R.string.toast_already_connected));
//                    } else {
//                        scanBleDeviceHelper.getRemoteDeviceByAddress(device.bleAddress);
//                    }
//                }
            }

            @Override
            public void onEditBtnClick(MyBleDevice device) {

            }

            @Override
            public void onSaveBtnClick(MyBleDevice device) {
                MyDatabase.getDatabase().getMyBleDeviceDao().insert(device);
                MyApplication.showSuccessDialog(mActivity, getString(R.string.save_success));
                if (mConnectDevice != null) {
                    if (device.bleAddress.equals(mConnectDevice.getAddress())) {
                        tvCurrentDeviceName.setText(device.updateName);
                    }
                }

                List<MyDevice> list = bluetoothScanListAdapter.getData();
                for (MyDevice item : list) {
                    if (item.getDevice().getAddress().equals(device.bleAddress)) {
                        item.setUpdateName(device.updateName);
                    }
                }
                bluetoothScanListAdapter.setData(list);
            }

            @Override
            public void onDeleteBtnClick(MyBleDevice device) {
                MyDatabase.getDatabase().getMyBleDeviceDao().delete(device);
                MyApplication.showSuccessDialog(mActivity, getString(R.string.delete_success));
                queryHisotryBluetoothDeviceList();

                // 如果修改的是当前连接的蓝牙, 则把名字换成原来的名字
                if (mConnectDevice != null) {
                    String bleAddress = device.bleAddress;
                    String currentDeviceAddress = mConnectDevice.getAddress();
                    if (bleAddress.equals(currentDeviceAddress)) {
                        tvCurrentDeviceName.setText(device.bleName);
                    }
                }


                // 重新渲染扫描到的列表
                List<MyDevice> list = bluetoothScanListAdapter.getData();
                // 比对数据库, 查看蓝牙是否有别名
                for (MyDevice myDevice : list) {
                    String address = myDevice.getDevice().getAddress();
                    List<MyBleDevice> dataBaselist = MyDatabase.getDatabase().getMyBleDeviceDao().queryByAddress(address);
                    if (dataBaselist != null && !dataBaselist.isEmpty()) {
                        myDevice.setUpdateName(dataBaselist.get(0).updateName);
                    } else {
                        myDevice.setUpdateName("");
                    }
                }
                bluetoothScanListAdapter.setData(list);
            }
        });

    }


    /**
     * 渲染扫描到的蓝牙设备列表
     */
    public void initScanBleDeviceList() {

        rvBleScanList = findViewById(R.id.rvBleScanList);
        tableNoDataBleScanList = findViewById(R.id.tableNoDataBleScanList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        bluetoothScanListAdapter = new BluetoothScanListAdapter();
        rvBleScanList.setLayoutManager(layoutManager);
        rvBleScanList.setAdapter(bluetoothScanListAdapter);
        bluetoothScanListAdapter.setListener(new BluetoothScanListAdapter.onClickListener() {
            @Override
            public void onConnectBtnClick(MyDevice device) {
                scanBleDeviceHelper.getRemoteDeviceByAddress(device.getDevice().getAddress());
            }
        });

    }


    /**
     * 渲染扫描按钮
     */
    public void initScanBtn() {

        tvScanBtn = findViewById(R.id.tvScanBtn);

        // 扫描蓝牙设备
        tvScanBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                scanBleDeviceHelper.scanBleDevice();
                MyApplication.showLoadingDialog(mActivity, getString(R.string.toast_bluetooth_scanning), 4800);
                // tvScanBtn.setClickable(false);
            }
        });

    }


    /**
     * 初始化scanBleDeviceHelper
     */
    public void initScanBleDeviceHelper() {
        scanBleDeviceHelper = new ScanBleDeviceHelper(mActivity, new ScanBleDeviceCallback() {
            @Override
            public void showDevice(MyDevice device) {

            }

            @Override
            public void showError(int code) {

            }

            @Override
            public void showAllDevice(List<MyDevice> deviceList) {
                if (deviceList == null || deviceList.isEmpty()) {
                    MyApplication.showFailedDialog(mActivity, getString(R.string.toast_no_bluetooth_device));
                    tableNoDataBleScanList.setVisible(View.VISIBLE);
                    rvBleScanList.setVisibility(View.GONE);
                    return;
                }

                List<MyDevice> filteredList = new ArrayList<>();
                for (MyDevice myDevice : deviceList) {
                    if (myDevice.getDevice() != null && !TextUtils.isEmpty(myDevice.getDevice().getName()) && myDevice.getDevice().getName().startsWith("HS")) {
                        filteredList.add(myDevice);
                    }
                }

                if (filteredList.isEmpty()) {
                    MyApplication.showFailedDialog(mActivity, getString(R.string.toast_no_bluetooth_device));
                    tableNoDataBleScanList.setVisible(View.VISIBLE);
                    rvBleScanList.setVisibility(View.GONE);
                    return;
                }


                // 比对数据库, 查看蓝牙是否有别名
                for (MyDevice myDevice : filteredList) {
                    String address = myDevice.getDevice().getAddress();
                    List<MyBleDevice> list = MyDatabase.getDatabase().getMyBleDeviceDao().queryByAddress(address);
                    if (list != null && !list.isEmpty()) {
                        MyBleDevice myBleDevice = list.get(0);
                        myDevice.setUpdateName(list.get(0).updateName);

                        String oldBleName = myBleDevice.bleName;
                        String newBleName = myDevice.getDevice().getName();
                        // 比对数据库中的蓝牙广播名称和最新的蓝牙广播名称, 如果有更新, 则更新数据库
                        if (!newBleName.equals(oldBleName)) {
                            myBleDevice.bleName = newBleName;
                            MyDatabase.getDatabase().getMyBleDeviceDao().insert(myBleDevice);
                            queryHisotryBluetoothDeviceList();
                        }
                    }
                }


                bluetoothScanListAdapter.setData(filteredList);
                tableNoDataBleScanList.setVisible(View.GONE);
                rvBleScanList.setVisibility(View.VISIBLE);
                MyApplication.showSuccessDialog(mActivity, getString(R.string.toast_scan_success));

            }

            @Override
            public void showRemoteDevice(BluetoothDevice device) {
                //先断开其它蓝牙连接
                BleHelper.getInstance().disconnectOtherGatt();
                BleHelper.getInstance().connectGatt(device);
            }

            @Override
            public void requestPermissionFailed() {
//                System.exit(0);
            }
        });

    }


    /**
     * 数据库查询已连接过的蓝牙设备
     */
    public void queryHisotryBluetoothDeviceList() {
        myBleDeviceList.clear();
        myBleDeviceList = MyDatabase.getDatabase().getMyBleDeviceDao().selectAll();
        if (myBleDeviceList != null && !myBleDeviceList.isEmpty()) {
            rvHistoryDevice.setVisibility(View.VISIBLE);
            tableNoDataHistoryDevice.setVisible(View.GONE);
            bluetoothHistoryListAdapter.setData(myBleDeviceList);

        } else {
            rvHistoryDevice.setVisibility(View.GONE);
            tableNoDataHistoryDevice.setVisible(View.VISIBLE);
        }
    }


    /**
     * 监听蓝牙连接成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectSuccessEvent(EventManager.OnBleConnectSuccessEvent event) {

        // 查询当前蓝牙, 是否有别名, 有别名则展示别名
        mConnectDevice = BleHelper.getInstance().getCurrentBluetoothDevice();
        if (mConnectDevice != null) {
            List<MyBleDevice> list = MyDatabase.getDatabase().getMyBleDeviceDao().queryByAddress(mConnectDevice.getAddress());
            if (list != null && !list.isEmpty()) {
                String name = TextUtils.isEmpty(list.get(0).updateName) ? mConnectDevice.getName() : list.get(0).updateName;
                tvCurrentDeviceName.setText(name);
            } else {
                tvCurrentDeviceName.setText(mConnectDevice.getName());
                MyDatabase.getDatabase().getMyBleDeviceDao().insert(new MyBleDevice(
                        mConnectDevice.getAddress(),
                        mConnectDevice.getName(),
                        ""
                ));
                queryHisotryBluetoothDeviceList();
            }
        }
    }


    /**
     * 监听蓝牙断开
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDisconnectEvent(EventManager.OnBleDisconnectEvent event) {
        Log.e(getClass().getSimpleName(), "监听到蓝牙连接状态改变 - 断开" + BleHelper.getInstance().getCurrentBluetoothDevice().getName() + " - " + getString(R.string.disconnected));
        tvCurrentDeviceName.setText("--");
    }

    /**
     * 监听蓝牙连接失败
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnBleConnectFailedEvent(EventManager.OnBleConnectFailedEvent event) {
        Log.e(getClass().getSimpleName(), "监听到蓝牙连接状态改变 - 失败" + BleHelper.getInstance().getCurrentBluetoothDevice().getName() + " - " + getString(R.string.bluetooth_connect_failed));
        tvCurrentDeviceName.setText("--");
    }


    @Override
    protected boolean isTitleBar() {
        return true;
    }

    @Override
    protected String getTitleMsg() {
        return getString(R.string.manage_bluetooth_device_title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, BluetoothManageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }
}
