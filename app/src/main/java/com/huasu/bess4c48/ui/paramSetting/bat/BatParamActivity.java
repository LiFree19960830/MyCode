package com.huasu.bess4c48.ui.paramSetting.bat;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseSettingParamActivity;
import com.huasu.bess4c48.bean.BTGRealtimeData;
import com.huasu.bess4c48.bean.CommandData;
import com.huasu.bess4c48.bean.ParentParam;
import com.huasu.bess4c48.callback.MyUiCallback;
import com.huasu.bess4c48.event.EventManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BatParamActivity extends BaseSettingParamActivity {

    private RelativeLayout rlVolt, rlTemp, rlCurrent;

    public void initView() {
        super.initView();
        rlVolt = findViewById(R.id.rlVolt);
        rlTemp = findViewById(R.id.rlTemp);
        rlCurrent = findViewById(R.id.rlCurrent);
        rlVolt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoltParamActivity.launchActivity();
            }
        });
        rlCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentParamActivity.launchActivity();
            }
        });
        rlTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TempParamActivity.launchActivity();
            }
        });
        readParam(false);
    }

    public void readParam(boolean showToast) {
        if (isBluetoothConnected(showToast)) {
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_bat_param, (byte) 0x00, (byte) 0x63, 4, "查询电池信息", showToast);
        }
    }

    public int getAddVal(){
        return 0x8000;
    }

    public void readParam(boolean showToast, int position) {
        readParam(showToast);
    }

    public List<ParentParam> init() {
        List<ParentParam> list = new ArrayList<>();
        //放电过流
        ParentParam item = new ParentParam();
        item.isShowReadBtn = true;
        item.title = getString(R.string.battery_info);
        item.childs.add(getDeviceParam(R.string.battery_type, (byte) 0x00, (byte) 0x63, "--", "--",
                "", "1", 0, 0, 0, isSetting(false), "battery_type_"));//电池类型
        item.childs.add(getDeviceParam(R.string.battery_series_count, (byte) 0x00, (byte) 0x64, "--", "--",
                "", "1", 0, 0, 1, isSetting(false), ""));//电池串数
        item.childs.add(getDeviceParam(R.string.battery_capacity, (byte) 0x00, (byte) 0x66, "--", "--",
                "AH", "0.01", 0, 0, 3, isSetting(false), ""));//电池容量
        item.childs.add(getDeviceParam(R.string.temperature_count, (byte) 0x00, (byte) 0x65, "--", "--",
                "", "1", 0, 0, 2, isSetting(false), ""));//温度个数
        item.childs.add(getDeviceParam(R.string.soh, (byte) 0x01, (byte) 0x21, "0", "100.0",
                "%", "0.1", 0, 1, -1, isSetting(false), ""));//SOH
        item.childs.add(getDeviceParam(R.string.soc, (byte) 0x00, (byte) 0xC9, "0", "100.0",
                "%", "0.1", 0, 1, -1, 1, ""));//SOC
        list.add(item);
        return list;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_bat_param;
    }

    @Override
    protected boolean isTitleBar() {
        return true;
    }

    @Override
    protected String getTitleMsg() {
        return getString(R.string.battery_parameters);
    }


    /**
     * 启动页面
     **/
    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, BatParamActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }

    /**
     * 收到参数
     */
    public void refreshParam(int commandType, byte[] receiveArray) {
        if (commandType == CommandData.command_type_bat_param) {
            getParamHexVal(0, receiveArray);
            mCommonParamAdapter.notifyItemRangeChanged(0, 1);
        }
    }


    /**
     * 监听电池组实时数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowBtgRealtimeDataEvent(EventManager.OnShowBtgRealtimeDataEvent event) {
        // 获取电池组数据
        BTGRealtimeData btgRealtimeData = event.getBTGRealtimeData();
        ParentParam parentParam = mCommonParamAdapter.getItem(0);
        parentParam.childs.get(4).convertVal = String.valueOf(btgRealtimeData.soh);
        parentParam.childs.get(5).convertVal = String.valueOf(btgRealtimeData.soc);
        mCommonParamAdapter.notifyItemRangeChanged(0, 1);
    }

}
