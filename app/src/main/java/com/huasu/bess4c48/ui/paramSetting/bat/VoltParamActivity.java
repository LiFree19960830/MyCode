package com.huasu.bess4c48.ui.paramSetting.bat;

import android.content.Intent;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseSettingParamActivity;
import com.huasu.bess4c48.bean.CommandData;
import com.huasu.bess4c48.bean.ParentParam;
import com.huasu.bess4c48.callback.MyUiCallback;

import java.util.ArrayList;
import java.util.List;

public class VoltParamActivity extends BaseSettingParamActivity {


    public void readParam(boolean showToast) {
        if (isBluetoothConnected(showToast)) {
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_bat_volt_param, (byte) 0x00, (byte) 0x6F, 14, "开始查询电池电压参数", showToast);
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_bat_failure_param, (byte) 0x00, (byte) 0x2F, 3, "开始查询电芯失效参数", false);
        }
    }

    public List<ParentParam> init() {
        List<ParentParam> list = new ArrayList<>();
        //单体电压低
        ParentParam item = new ParentParam();
        item.title = getString(R.string.cell_voltage_low);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x71, "1.800", "3.300",
                "V", "0.001", 0,3, 2,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x72, "1.800", "3.300",
                "V", "0.001", 0,3, 3,isSetting(true),""));//保护恢复值
        list.add(item);

        //单体电压高
        item = new ParentParam();
        item.title = getString(R.string.cell_voltage_high);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x6F, "2.900", "4.500",
                "V", "0.001", 0,3, 0,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x70, "2.900", "4.500",
                "V", "0.001", 0,3, 1,isSetting(true),""));//保护恢复值
        list.add(item);

        //总压低
        item = new ParentParam();
        item.title = getString(R.string.total_voltage_low);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x7B, "0", "55.00",
                "V", "0.01", 0,2, 12,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x7C, "0", "55.00",
                "V", "0.01", 0,2, 13,isSetting(true),""));//保护恢复值
        list.add(item);

        //总压高
        item = new ParentParam();
        item.title = getString(R.string.total_voltage_high);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x79, "0", "70.00",
                "V", "0.01", 0,2, 10,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x7A, "0", "70.00",
                "V", "0.01", 0,2, 11,isSetting(true),""));//保护恢复值
        list.add(item);

        //单体电压不均
        item = new ParentParam();
        item.title = getString(R.string.cell_voltage_uneven);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x73, "0", "3.000",
                "V", "0.001", 0,3, 4,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x74, "0", "3.000",
                "V", "0.001", 0,3, 5,isSetting(true),""));//保护恢复值
        list.add(item);

        //电芯失效
        item = new ParentParam();
        item.title = getString(R.string.cell_failure);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x2F, "0", "5.000",
                "V", "0.001", 0,3, 0,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x31, "0", "5.000",
                "V", "0.001", 0,3, 2,isSetting(true),""));//保护恢复值

        list.add(item);
        return list;
    }


    @Override
    protected String getTitleMsg() {
        return getString(R.string.voltage_category);
    }

    /**
     * 启动页面
     **/
    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, VoltParamActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }

    /**
     * 收到参数
     */
    public void refreshParam(int commandType, byte[] receiveArray) {
        if (commandType == CommandData.command_type_bat_volt_param || commandType == CommandData.command_type_bat_failure_param) {
            if (commandType == CommandData.command_type_bat_volt_param) {
                for (int i = 0; i < 5; i++) {
                    getParamHexVal(i, receiveArray);
                }
                mCommonParamAdapter.notifyItemRangeChanged(0, 5);
            } else {
                getParamHexVal(5, receiveArray);
                mCommonParamAdapter.notifyItemRangeChanged(5, 1);
            }
        }
    }


}
