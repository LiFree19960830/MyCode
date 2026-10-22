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

public class TempParamActivity extends BaseSettingParamActivity {


    private int[] params1, params2;

    public void readParam(boolean showToast) {
        if (isBluetoothConnected(showToast)) {
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_bat_temp_param1, (byte) 0x00, (byte) 0xA1, 8, "查询电池温度参数1", showToast);
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_bat_temp_param2, (byte) 0x00, (byte) 0x8D, 16, "查询电池温度参数2", false);
        }
    }

    public List<ParentParam> init() {
        List<ParentParam> list = new ArrayList<>();
        //放电温度低
        ParentParam item = new ParentParam();
        item.title = getString(R.string.discharge_temperature_low);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0xA3, "-50.0", "30.0",
                "℃", "0.1", 500, 1, 2,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0xA4, "-50.0", "30.0",
                "℃", "0.1", 500, 1, 3,isSetting(true),""));//保护恢复值
        list.add(item);

        //放电温度高
        item = new ParentParam();
        item.title = getString(R.string.discharge_temperature_high);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0xA1, "-50.0", "100.0",
                "℃", "0.1", 500, 1, 0,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0xA2, "-50.0", "100.0",
                "℃", "0.1", 500, 1, 1,isSetting(true),""));//保护恢复值
        list.add(item);

        //充电温度低
        item = new ParentParam();
        item.title = getString(R.string.charge_temperature_low);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x9B, "-50.0", "30.0",
                "℃", "0.1", 500, 1, 14,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x9C, "-50.0", "30.0",
                "℃", "0.1", 500, 1, 15,isSetting(true),""));//保护恢复值
        list.add(item);

        //充电温度高
        item = new ParentParam();
        item.title = getString(R.string.charge_temperature_high);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x99, "-50.0", "100.0",
                "℃", "0.1", 500, 1, 12,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x9A, "-50.0", "100.0",
                "℃", "0.1", 500, 1, 13,isSetting(true),""));//保护恢复值
        list.add(item);

        //单体温度不均
        item = new ParentParam();
        item.title = getString(R.string.cell_temperature_uneven);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0xA7, "0", "50.0",
                "℃", "0.1", 0,1, 6,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0xA8, "0", "50.0",
                "℃", "0.1", 0,1, 7,isSetting(true),""));//保护恢复值
        list.add(item);

        //环境温度高
        item = new ParentParam();
        item.title = getString(R.string.environment_temperature_high);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x8D, "-50.0", "100.0",
                "℃", "0.1", 500, 1, 0,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x8E, "-50.0", "100.0",
                "℃", "0.1", 500, 1, 1,isSetting(true),""));//保护恢复值
        list.add(item);

        //环境温度低
        item = new ParentParam();
        item.title = getString(R.string.environment_temperature_low);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x8F, "-50.0", "30.0",
                "℃", "0.1", 500, 1, 2,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x90, "-50.0", "30.0",
                "℃", "0.1", 500, 1, 3,isSetting(true),""));//保护恢复值
        list.add(item);

        //MOS高温
        item = new ParentParam();
        item.title = getString(R.string.mos_high_temperature);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x93, "-50.0", "150.0",
                "℃", "0.1", 500, 1, 6,isSetting(true),""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x94, "-50.0", "150.0",
                "℃", "0.1", 500, 1, 7,isSetting(true),""));//保护恢复值

        list.add(item);
        params1 = new int[]{0, 1, 4};
        params2 = new int[]{2, 3, 5, 6, 7};
        return list;
    }


    @Override
    protected String getTitleMsg() {
        return getString(R.string.temperature_category);
    }

    /**
     * 启动页面
     **/
    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, TempParamActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }

    /**
     * 收到参数
     */
    public void refreshParam(int commandType, byte[] receiveArray) {
        if (commandType == CommandData.command_type_bat_temp_param1 || commandType == CommandData.command_type_bat_temp_param2) {
            if (commandType == CommandData.command_type_bat_temp_param1) {
                for (int i = 0; i < params1.length; i++) {
                    getParamHexVal(params1[i], receiveArray);
                }
                mCommonParamAdapter.notifyDataSetChanged();
            } else {
                for (int i = 0; i < params2.length; i++) {
                    getParamHexVal(params2[i], receiveArray);
                }
                mCommonParamAdapter.notifyDataSetChanged();
            }
        }
    }


}
