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

public class CurrentParamActivity extends BaseSettingParamActivity {


    public void readParam(boolean showToast) {
        if (isBluetoothConnected(showToast)) {
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_bat_current_param, (byte) 0x00, (byte) 0x85, 4, "查询电池电流参数", showToast);
        }
    }

    public List<ParentParam> init() {
        List<ParentParam> list = new ArrayList<>();
        //放电过流
        ParentParam item = new ParentParam();
        item.title = getString(R.string.discharge_over_current);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x87, "-300.00", "0",
                "A", "0.01", 30000, 2, 2, isSetting(true), ""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x88, "-300.00", "0",
                "A", "0.01", 30000, 2, 3, isSetting(true), ""));//保护恢复值
        list.add(item);

        //充电过流
        item = new ParentParam();
        item.title = getString(R.string.charge_over_current);
        item.childs.add(getDeviceParam(R.string.protection_value, (byte) 0x00, (byte) 0x85, "0", "300.00",
                "V", "0.01", 30000, 2, 0, isSetting(true), ""));//保护值
        item.childs.add(getDeviceParam(R.string.protection_recovery_value, (byte) 0x00, (byte) 0x86, "0", "300.00",
                "V", "0.01", 30000, 2, 1, isSetting(true), ""));//保护恢复值
        list.add(item);

        return list;
    }


    @Override
    protected String getTitleMsg() {
        return getString(R.string.current_category);
    }

    /**
     * 启动页面
     **/
    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, CurrentParamActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }

    /**
     * 收到参数
     */
    public void refreshParam(int commandType, byte[] receiveArray) {
        if (commandType == CommandData.command_type_bat_current_param) {
            for (int i = 0; i < mCommonParamAdapter.getItemCount(); i++) {
                getParamHexVal(i, receiveArray);
            }
            mCommonParamAdapter.notifyDataSetChanged();
        }
    }


}
