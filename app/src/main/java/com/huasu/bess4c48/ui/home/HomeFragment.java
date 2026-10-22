package com.huasu.bess4c48.ui.home;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.BTGRealtimeData;
import com.huasu.bess4c48.bean.MyBleDevice;
import com.huasu.bess4c48.bean.MyDevice;
import com.huasu.bess4c48.bean.SelectionData;
import com.huasu.bess4c48.callback.ScanBleDeviceCallback;
import com.huasu.bess4c48.database.MyDatabase;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.BleHelper;
import com.huasu.bess4c48.utils.CPResourceUtil;
import com.huasu.bess4c48.utils.ScanBleDeviceHelper;
import com.huasu.bess4c48.view.BtgSwitchBar;
import com.huasu.bess4c48.view.CircleProgressView;
import com.huasu.bess4c48.view.dialog.SelectionOneDialog;
import com.lihang.ShadowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的首页
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private BtgSwitchBar sbTotalV;
    private BtgSwitchBar sbTotalI;
    private BtgSwitchBar sbPower;
    private BtgSwitchBar sbCycCount;
    private BtgSwitchBar sbBatT;
    private BtgSwitchBar sbAvgV;
    private BtgSwitchBar sbMaxV;
    private BtgSwitchBar sbMinV;

    private ImageView ivHomeGif;
    private CircleProgressView mProgressView;
    private TextView tvProgress;

    private TextView tvBtgState;

    private ShadowLayout slChargeState;

    private TextView tvAlarm;
    private ShadowLayout slConnectBtn;

    private LinearLayout llbtgInfo;

    private ScanBleDeviceHelper scanBleDeviceHelper;

    private List<MyDevice> myDeviceList = new ArrayList<>();

    private SelectionOneDialog selectionOneDialog;

    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        EventBus.getDefault().register(this);
        scanBleDeviceHelper = new ScanBleDeviceHelper(this.getActivity(), new ScanBleDeviceCallback() {
            @Override
            public void showDevice(MyDevice device) {

            }

            @Override
            public void showError(int code) {
                slConnectBtn.setClickable(true);
            }

            @Override
            public void showAllDevice(List<MyDevice> deviceList) {


                if (deviceList == null) {
                    MyApplication.showFailedDialog(getActivity(), getString(R.string.toast_no_bluetooth_device));
                    slConnectBtn.setClickable(true);
                    return;
                }

                final ArrayList<SelectionData> selectionDataArrayList = new ArrayList<>();
                for (MyDevice myDevice : deviceList) {
                    if (myDevice.getDevice() != null
                            && !TextUtils.isEmpty(myDevice.getDevice().getName()) && myDevice.getDevice().getName().startsWith("HS")) {

                        // 比对数据库, 查看蓝牙是否有别名
                        String name = update(myDevice);
                        String address = myDevice.getDevice().getAddress();
                        selectionDataArrayList.add(new SelectionData(
                                address,
                                name,
                                myDevice
                        ));
                    }
                }

                if (selectionDataArrayList.isEmpty()) {
                    MyApplication.showFailedDialog(getActivity(), getString(R.string.toast_no_bluetooth_device));
                    slConnectBtn.setClickable(true);
                    return;
                }

                MyApplication.showSuccessDialog(getActivity(), getString(R.string.toast_scan_success));
                myDeviceList.clear();
                myDeviceList.addAll(deviceList);
                slConnectBtn.setClickable(true);
                selectionOneDialog = SelectionOneDialog.newInstance(selectionDataArrayList, getString(R.string.bluetooth_select), -1, new SelectionOneDialog.OnViewClickListener() {
                    @Override
                    public void onSelect(int checkPosition) {

                    }

                    @Override
                    public void dismiss() {
                        selectionOneDialog = null;
                    }

                    @Override
                    public void onSure(int checkPosition, SelectionData checkedItem) {
                        if (checkedItem != null) {
                            MyDevice myDevice = selectionDataArrayList.get(checkPosition).myDevice;
//                            BleHelper.getInstance().connectGatt(myDevice.getDevice());

                            scanBleDeviceHelper.getRemoteDeviceByAddress(myDevice.getDevice().getAddress());
                            dismiss();
                        } else {
                            MyApplication.showSuccessDialog(getActivity(), getString(R.string.bluetooth_select));
                        }

                    }
                });
                selectionOneDialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), getContext().getClass().getSimpleName());

            }

            @Override
            public void showRemoteDevice(BluetoothDevice device) {
                BleHelper.getInstance().connectGatt(device);
            }

            @Override
            public void requestPermissionFailed() {
//                System.exit(0);
//                MyApplication.getInstance().removeALLActivity();
                slConnectBtn.setClickable(true);
            }
        });

        Log.d(TAG, "onCreateView: ");
        return view;
    }

    private String update(MyDevice myDevice) {
        String address = myDevice.getDevice().getAddress();
        String name = myDevice.getDevice().getName();
        List<MyBleDevice> list = MyDatabase.getDatabase().getMyBleDeviceDao().queryByAddress(address);
        if (list != null && !list.isEmpty()) {
            MyBleDevice myBleDevice = list.get(0);
            String oldBleName = myBleDevice.bleName;
            String newBleName = myDevice.getDevice().getName();
            // 比对数据库中的蓝牙广播名称和最新的蓝牙广播名称, 如果有更新, 则更新数据库
            if (!newBleName.equals(oldBleName)) {
                myBleDevice.bleName = newBleName;
                MyDatabase.getDatabase().getMyBleDeviceDao().insert(myBleDevice);
            }
            // 用户自定义名称存在时, 使用用户自定义的名称, 否则显示最新的蓝牙广播名称
            name = TextUtils.isEmpty(list.get(0).updateName) ? newBleName : list.get(0).updateName;
        }
        return name;
    }


    public void initView(View view) {
        ivHomeGif = view.findViewById(R.id.ivHomeGif);
        tvBtgState = view.findViewById(R.id.tvBtgState);
        sbTotalV = view.findViewById(R.id.sbTotalV);
        sbTotalI = view.findViewById(R.id.sbTotalI);
        sbPower = view.findViewById(R.id.sbPower);
        sbCycCount = view.findViewById(R.id.sbCycCount);
        sbBatT = view.findViewById(R.id.sbBatT);
        sbAvgV = view.findViewById(R.id.sbAvgV);
        sbMaxV = view.findViewById(R.id.sbMaxV);
        sbMinV = view.findViewById(R.id.sbMinV);
        slConnectBtn = view.findViewById(R.id.slConnectBtn);
        llbtgInfo = view.findViewById(R.id.llbtgInfo);
        mProgressView = view.findViewById(R.id.circleProgress);
        tvProgress = view.findViewById(R.id.tvProgress);

        slChargeState = view.findViewById(R.id.slChargeState);
        tvAlarm = view.findViewById(R.id.tvAlarm);

        Glide.with(this).load(R.drawable.home).into(ivHomeGif);

        slConnectBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                scanBleDeviceHelper.scanBleDevice();
                // MyApplication.showToastLoading("蓝牙扫描中...");
                MyApplication.showLoadingDialog(getActivity(), MyApplication.mApplication.getResources().getString(R.string.toast_bluetooth_scanning), 20000);
                slConnectBtn.setClickable(false);
            }
        });

        tvAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventManager.OnFragmentSwitchEvent(2));
            }
        });
    }

    /**
     * 监听电池组实时数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowBtgRealtimeDataEvent(EventManager.OnShowBtgRealtimeDataEvent event) {
        BTGRealtimeData btgRealtimeData = event.getBTGRealtimeData();
        sbTotalV.setStatus(btgRealtimeData.getVolt());
        sbTotalI.setStatus(btgRealtimeData.getCurrent());
        sbPower.setStatus(btgRealtimeData.getPower());
        sbCycCount.setStatus(btgRealtimeData.getChgCyclesString());
        sbBatT.setStatus(btgRealtimeData.getAvgTemp());
        sbAvgV.setStatus(btgRealtimeData.getAvgVolt());
        sbMaxV.setStatus(btgRealtimeData.getMaxVolt());
        sbMinV.setStatus(btgRealtimeData.getMinVolt());
        mProgressView.setProgress(btgRealtimeData.getSOC());

        tvProgress.setText(btgRealtimeData.getSOCStr());

        slChargeState.setVisibility(View.VISIBLE);
        tvBtgState.setText(CPResourceUtil.getChargeStateInfoByCode(btgRealtimeData.chargeStatus));

        if (btgRealtimeData.alarms.size() > 0) {
            tvAlarm.setVisibility(View.VISIBLE);
        } else {
            tvAlarm.setVisibility(View.GONE);
        }
    }

    /**
     * 监听蓝牙连接成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectSuccessEvent(EventManager.OnBleConnectSuccessEvent event) {
        Log.d(TAG, "onBleConnectSuccessEvent: 数据清空");
        ivHomeGif.setVisibility(View.GONE);
        slConnectBtn.setVisibility(View.GONE);
        llbtgInfo.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        slChargeState.setVisibility(View.GONE);
        tvAlarm.setVisibility(View.GONE);
        sbTotalV.setStatus("--");
        sbTotalI.setStatus("--");
        sbPower.setStatus("--");
        sbCycCount.setStatus("--");
        sbBatT.setStatus("--");
        sbAvgV.setStatus("--");
        sbMaxV.setStatus("--");
        sbMinV.setStatus("--");

        // 查询当前蓝牙连接是否已经入库, 如果没有, 则进行入库;
        BluetoothDevice mBleDevice = BleHelper.getInstance().getCurrentBluetoothDevice();
        List<MyBleDevice> list = MyDatabase.getDatabase().getMyBleDeviceDao().queryByAddress(mBleDevice.getAddress());
        if (list == null || list.isEmpty()) {
            MyDatabase.getDatabase().getMyBleDeviceDao().insert(new MyBleDevice(
                    mBleDevice.getAddress(),
                    mBleDevice.getName(),
                    ""
            ));
        }
    }


    /**
     * 监听蓝牙断开
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDisconnectEvent(EventManager.OnBleDisconnectEvent event) {
        Log.d(TAG, "onBleDisconnectEvent: 蓝牙断开");
        slConnectBtn.setVisibility(View.VISIBLE);
        llbtgInfo.setVisibility(View.GONE);
        slChargeState.setVisibility(View.GONE);
        tvAlarm.setVisibility(View.GONE);
        ivHomeGif.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.GONE);

        sbTotalV.setStatus("--");
        sbTotalI.setStatus("--");
        sbPower.setStatus("--");
        sbCycCount.setStatus("--");
        sbBatT.setStatus("--");
        sbAvgV.setStatus("--");
        sbMaxV.setStatus("--");
        sbMinV.setStatus("--");
    }


    /**
     * 监听蓝牙连接失败
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectFailedEvent(EventManager.OnBleConnectFailedEvent event) {
        slConnectBtn.setVisibility(View.VISIBLE);
        llbtgInfo.setVisibility(View.GONE);
        slChargeState.setVisibility(View.GONE);
        tvAlarm.setVisibility(View.GONE);
        ivHomeGif.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.GONE);

        sbTotalV.setStatus("--");
        sbTotalI.setStatus("--");
        sbPower.setStatus("--");
        sbCycCount.setStatus("--");
        sbBatT.setStatus("--");
        sbAvgV.setStatus("--");
        sbMaxV.setStatus("--");
        sbMinV.setStatus("--");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}