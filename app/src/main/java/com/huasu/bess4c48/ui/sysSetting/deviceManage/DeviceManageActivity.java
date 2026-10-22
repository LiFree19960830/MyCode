package com.huasu.bess4c48.ui.sysSetting.deviceManage;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseSettingParamActivity;
import com.huasu.bess4c48.bean.CommandData;
import com.huasu.bess4c48.bean.DeviceParam;
import com.huasu.bess4c48.bean.ParentParam;
import com.huasu.bess4c48.bean.SelectDate;
import com.huasu.bess4c48.callback.MyUiCallback;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.ui.sysSetting.bluetooth.BluetoothManageActivity;
import com.huasu.bess4c48.utils.ByteUtils;
import com.huasu.bess4c48.utils.ToolUtil;
import com.huasu.bess4c48.view.dialog.BaseToastDialog;
import com.huasu.bess4c48.view.dialog.DatePickerDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DeviceManageActivity extends BaseSettingParamActivity {
    public SelectDate mDate;
    public RelativeLayout rlBluetooth;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_device_manage;
    }

    @Override
    public void initView() {
        super.initView();
        rlBluetooth = findViewById(R.id.rlBluetooth);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mDate = new SelectDate(year, month, day, hour, minute);
        btnRestore.setVisibility(View.GONE);
        rlBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothManageActivity.launchActivity();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(getClass().getSimpleName(), "onRestart");
        readParam(false);
    }


    @Override
    public void readParam(boolean showToast) {
        if (isBluetoothConnected(showToast)) {
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_device_manage_param1, (byte) 0x00, (byte) 0x00, 13, "查询设备信息1", showToast);
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_device_manage_param2, (byte) 0x00, (byte) 0xC0, 3, "查询BMS时间", false);
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_device_manage_param3, (byte) 0x02, (byte) 0x00, 10, "查询设备信息3", false);
            // MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_device_manage_param4, (byte) 0x00, (byte) 0xEA, 12, "查询设备信息4", false);

        }
    }

    //点击child item
    protected void onChildItemClick(int parentPosition, int childPosition, DeviceParam child, String parentName) {
        if (isBluetoothConnected(true)) {
            showDialog();
        }
    }

    private BaseToastDialog mBaseToastDialog;

    private void showDialog() {
        mBaseToastDialog = BaseToastDialog.newInstance(
                new BaseToastDialog.OnViewClickListener() {

                    @Override
                    public void dismiss() {
                        mBaseToastDialog = null;
                    }

                    @Override
                    public void submit() {
                        mBaseToastDialog = null;
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DATE);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);
                        MyUiCallback.getInstance().sendWriteCommand(CommandData.command_type_setting, (byte) 0x00, (byte) 0xC0, "正在设置BMS时间", true, 4,
                                year - 2000,
                                month,
                                day,
                                hour,
                                minute, second);
                        Log.e("正在设置BMS时间", year - 2000 + ";" + month + ";" + day + ";" + hour + ";" + minute
                                + ";" + second);
                    }
                }, getString(R.string.set_phone_time));
        mBaseToastDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), mContext.getClass().getSimpleName());

    }

    @Override
    public List<ParentParam> init() {
        List<ParentParam> list = new ArrayList<>();
        //放电过流
        ParentParam item = new ParentParam();
        item.isShowReadBtn = true;
        item.title = getString(R.string.battery_info);
        item.isShowTab = false;
        item.childs.add(getDeviceParam(R.mipmap.icon_1, R.string.app_version_title, (byte) 0x00, (byte) 0x00, "--", "--",
                "", "1", 0, 0, -1, isSetting(false), ""));//APP版本
        item.childs.add(getDeviceParam(R.mipmap.icon_2, R.string.program_version_title, (byte) 0x00, (byte) 0x06, "--", "--",
                "", "1", 0, 0, 6, isSetting(false), ""));//程序版本 0X06 000B 000C
        item.childs.add(getDeviceParam(R.mipmap.icon_2, R.string.hardware_version_title, (byte) 0x00, (byte) 0x00, "--", "--",
                "", "1", 0, 0, 0, isSetting(false), ""));//硬件版本号
        item.childs.add(getDeviceParam(R.mipmap.icon_2, R.string.project_code_title, (byte) 0x00, (byte) 0x0A, "--", "--",
                "", "1", 0, 0, 10, isSetting(false), ""));//项目编码
        list.add(item);
        item.childs.get(0).convertVal = ToolUtil.getVerName();

        item = new ParentParam();
        item.isShowReadBtn = true;
        item.title = getString(R.string.battery_info);
        item.isShowTab = false;
        item.childs.add(getDeviceParam(R.mipmap.icon_1, R.string.boot_version_title, (byte) 0x00, (byte) 0x05, "--", "--",
                "", "1", 0, 0, 5, isSetting(false), ""));//BOOT版本
        item.childs.add(getDeviceParam(R.mipmap.icon_2, R.string.bms_sn_title, (byte) 0x00, (byte) 0x00, "--", "--",
                "", "1", 0, 0, -1, isSetting(false), ""));//BMS SN
       /* item.childs.add(getDeviceParam(R.mipmap.icon_2, R.string.communication_module_sn_title, (byte) 0x00, (byte) 0x00, "--", "--",
                "AH", "0.01", 0, 0, -1, isSetting(false), ""));//通信模块SN*/
        item.childs.add(getDeviceParam(R.mipmap.icon_2, R.string.bms_time_setting_title, (byte) 0x00, (byte) 0xC0, "--", "--",
                "", "1", 0, 0, -1, isSetting(true), ""));//BMS时间 00C0  00C1 00C2
        list.add(item);
        return list;
    }

    /**
     * 收到参数
     */
    public void refreshParam(int commandType, byte[] receiveArray) {
        if (commandType == CommandData.command_type_device_manage_param1) {
            try {
                ParentParam parentParam;
                for (int i = 0; i < 2; i++) {
                    parentParam = mCommonParamAdapter.getItem(i);
                    if (parentParam.childs != null && !parentParam.childs.isEmpty()) {
                        for (DeviceParam item : parentParam.childs) {
                            if (item.position >= 0) {
                                if (item.position == 6) {
                                    int v1 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[12], receiveArray[13]}), 16);
                                    int v2 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[22], receiveArray[23]}), 16);
                                    int v3 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[24], receiveArray[25]}), 16);
                                    item.convertVal = v1 + "." + v2 + "." + v3;
                                } else {
                                    item.readHexVal = ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[item.position * 2], receiveArray[item.position * 2 + 1]});
                                    item.convertVal = item.convertHexToVal();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCommonParamAdapter.notifyItemRangeChanged(0, 2);
        }

        if (commandType == CommandData.command_type_device_manage_param2) {
            try {
                ParentParam parentParam = mCommonParamAdapter.getItem(1);
                if (parentParam.childs != null && !parentParam.childs.isEmpty()) {
                    DeviceParam item = parentParam.childs.get(parentParam.childs.size() - 1);
                    int v1 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{0x00, receiveArray[0]}), 16) + 2000;
                    int v2 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{0x00, receiveArray[1]}), 16);
                    int v3 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{0x00, receiveArray[2]}), 16);
                    int v4 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{0x00, receiveArray[3]}), 16);
                    int v5 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{0x00, receiveArray[4]}), 16);
                    int v6 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{0x00, receiveArray[5]}), 16);
                    mDate.year = v1;
                    mDate.month = v2;
                    mDate.day = v3;
                    mDate.hour = v4;
                    mDate.minute = v5;
                    mDate.second = v6;
                    item.convertVal = v1 + "-" + ToolUtil.formatNumber(v2) + "-" + ToolUtil.formatNumber(v3) + " " + ToolUtil.formatNumber(v4) + ":" + ToolUtil.formatNumber(v5) + ":" + ToolUtil.formatNumber(v6);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCommonParamAdapter.notifyItemRangeChanged(1, 1);
        }
        if (commandType == CommandData.command_type_device_manage_param3 || commandType == CommandData.command_type_device_manage_param4) {
            try {
                ParentParam parentParam = mCommonParamAdapter.getItem(1);
                if (parentParam.childs != null && !parentParam.childs.isEmpty()) {
                    DeviceParam item = parentParam.childs.get(commandType == CommandData.command_type_device_manage_param3 ? 1 : 2);
                    String bmsSN = "";
                    for (byte itemByte : receiveArray) {
                        bmsSN += (char) itemByte;
                    }
                    item.convertVal = bmsSN.trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCommonParamAdapter.notifyItemRangeChanged(1, 1);
        }
    }


    public static void main(String[] args) {
        byte[] bytes = new byte[]{0x48, 0x57, 0x2D, 0x31, 0x32, 0x30, 0x30, 0x20, 0x35, 0x33, 0x2E, 0x62, 0x69, 0x6E};
        String bmsSN = "";
        for (byte itemByte : bytes) {
            bmsSN += (char) itemByte;
        }
        System.out.println("bmsSN=" + bmsSN);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        System.out.println("bmsSN=" + year + ";" + month + ";" + day + ";" + hour + ";" + minute + ";" + second);
    }

    @Override
    protected String getTitleMsg() {
        return getString(R.string.setting_device_management);
    }

    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, DeviceManageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }

    /**
     * 收到设置完参数的返回，设置成功需要发送一次读取指定
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowSettingDeviceParamEvent(EventManager.OnShowSettingDeviceParamEvent event) {
        if (event.isShowToast() && isCurrentActivity())
            MyApplication.showToastSuccess(getString(event.isSuccess() ? R.string.finish_setting_params_successed : R.string.finish_setting_params_failed));
        //如果成功了，需要再读取一次当前寄存器的值
        if (event.isSuccess())
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_device_manage_param2, (byte) 0x00, (byte) 0xC0, 3, "查询BMS时间", false);
    }

}
