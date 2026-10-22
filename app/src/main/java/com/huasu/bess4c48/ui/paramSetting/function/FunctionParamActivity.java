package com.huasu.bess4c48.ui.paramSetting.function;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseSettingParamActivity;
import com.huasu.bess4c48.bean.BTGRealtimeData;
import com.huasu.bess4c48.bean.CommandData;
import com.huasu.bess4c48.bean.DeviceParam;
import com.huasu.bess4c48.bean.ParentParam;
import com.huasu.bess4c48.callback.MyUiCallback;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.ByteUtils;
import com.huasu.bess4c48.utils.ToolUtil;
import com.huasu.bess4c48.view.dialog.BaseToastDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class FunctionParamActivity extends BaseSettingParamActivity {

    private int[] childParams1, childParams2;
    private boolean isStorageControl;//是否强控
    private int mosStatus;//mos状态

    @Override
    public void initView() {
        super.initView();
        btnRead.setText(R.string.btn_system_sleep);
        btnRestore.setText(R.string.btn_system_restart);
        ToolUtil.inspectionAuthority(btnRead, View.GONE);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUiCallback.getInstance().sendWriteCommand(CommandData.command_type_setting, (byte) 0x00, (byte) 0xCD, 61680, "系统休眠", true, -1, -1);

            }
        });
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUiCallback.getInstance().sendWriteCommand(CommandData.command_type_setting, (byte) 0x00, (byte) 0xBF, 61680, "系统重启", true, -1, -1);
            }
        });
    }

    protected void onParentItemClick(int position) {
        if (MyApplication.getInstance().isSysAdmin())
            showDialog(true, isStorageControl ? getString(R.string.close_storage_control)
                    : getString(R.string.open_storage_control), null);
    }

    //点击child item
    protected void onChildItemClick(int parentPosition, int childPosition, DeviceParam child, String parentName) {
        if (parentPosition != 2) {
            showInputValDialog(parentPosition, childPosition, child, parentName);
            return;
        }
        if (!isStorageControl) {
            MyApplication.getInstance().showToastFailed(getString(R.string.open_storage_control_tips));
            return;
        }
        showDialog(false, child.checked ? String.format(getString(R.string.close_mos_tops), child.paramName)
                : String.format(getString(R.string.open_mos_tops), child.paramName), child);
    }

    private BaseToastDialog mBaseToastDialog;

    private void showDialog(final boolean isControl, String content, final DeviceParam child) {
        mBaseToastDialog = BaseToastDialog.newInstance(
                new BaseToastDialog.OnViewClickListener() {

                    @Override
                    public void dismiss() {
                        mBaseToastDialog = null;
                    }

                    @Override
                    public void submit() {
                        mBaseToastDialog = null;
                        if (isControl) {
                            MyUiCallback.getInstance().sendWriteCommand(CommandData.command_type_setting, (byte) 0x00, (byte) 0xCB, isStorageControl ? 0 : 1, "强控开关", true, -1, -1);
                        } else {
                            int mask = child.checked ? ToolUtil.setBitTo0(mosStatus, child.position) :
                                    ToolUtil.setBitTo1(mosStatus, child.position);
                            MyUiCallback.getInstance().sendWriteCommand(CommandData.command_type_setting, (byte) 0x00, (byte) 0xCA, mask, "开或者关" + child.paramName, true, -1, -1);
                        }
                    }
                }, content);
        mBaseToastDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), mContext.getClass().getSimpleName());

    }

    /**
     * 刚进入界面时读取一次参数
     *
     * @param showToast
     */
    public void readParam(boolean showToast) {
        if (isBluetoothConnected(showToast)) {
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_function_param1, (byte) 0x00, (byte) 0xA9, 2, "查询功能参数1", showToast);
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_function_param2, (byte) 0x00, (byte) 0xCF, 1, "查询功能参数2", false);
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_function_param3, (byte) 0x00, (byte) 0x55, 1, "查询功能参数3", false);
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_function_param4, (byte) 0x00, (byte) 0xCA, 2, "查询功能参数4", false);
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_function_param5, (byte) 0x02, (byte) 0x32, 8, "查询功能参数5", false);
        }
    }

    public List<ParentParam> init() {
        List<ParentParam> list = new ArrayList<>();
        //均衡设置
        ParentParam item = new ParentParam();
        item.title = getString(R.string.balance_setting_label);
        item.childs.add(getDeviceParam(R.string.balance_voltage_start, (byte) 0x00, (byte) 0XA9, "0", "5.000",
                "V", "0.001", 0, 3, 0, isSetting(true), ""));//均衡开启电
        item.childs.add(getDeviceParam(R.string.balance_voltage_diff_start, (byte) 0x00, (byte) 0xAA, "0", "1.000",
                "V", "0.001", 0, 3, 1, isSetting(true), ""));//均衡启动压差
        item.childs.add(getDeviceParam(R.string.balance_voltage_diff_end, (byte) 0x00, (byte) 0xCF, "0", "1.000",
                "V", "0.001", 0, 3, 0, isSetting(true), ""));//均衡结束压差
        list.add(item);

        //自动休眠设置
        item = new ParentParam();
        item.title = getString(R.string.auto_sleep_setting_label);
        item.childs.add(getDeviceParam(R.string.auto_sleep_time, (byte) 0x00, (byte) 0X55, "0", "43200",
                getString(R.string.minute), "1", 0, 0, 0, isSetting(true), ""));//自动休眠时间
        list.add(item);

        //MOS设置
        item = new ParentParam();
        item.title = getString(R.string.mos_setting_label);
        item.isShowControl = true;
        item.childs.add(getDeviceParam(R.string.charge_mos, (byte) 0x01, (byte) 0X2D, "", "",
                "", "1", 0, 0, 1, isSetting(true), "", 2));//充电MOS
        item.childs.add(getDeviceParam(R.string.discharge_mos, (byte) 0x01, (byte) 0X2D, "", "",
                "", "1", 0, 0, 2, isSetting(true), "", 2));//放电MOS
        item.childs.add(getDeviceParam(R.string.mos_current_limit, (byte) 0x01, (byte) 0X2D, "", "",
                "", "1", 0, 0, 5, isSetting(true), "", 2));//限流板MOS
        item.childs.add(getDeviceParam(R.string.mos_heating, (byte) 0x01, (byte) 0X2D, "", "",
                "", "1", 0, 0, 4, isSetting(true), "", 2));//加热MOS
        list.add(item);

        //充放电管理
        item = new ParentParam();
        item.title = getString(R.string.cdc_manage);
        item.childs.add(getDeviceParam(R.string.request_charge_current, (byte) 0x02, (byte) 0X37, "", "",
                "A", "0.01", 0, 2, 5, 0, ""));//请求充电电流
        item.childs.add(getDeviceParam(R.string.request_discharge_current, (byte) 0x02, (byte) 0X38, "", "",
                "A", "0.01", 0, 2, 6, 0, ""));//请求放电电流
        item.childs.add(getDeviceParam(R.string.request_charge_voltage, (byte) 0x02, (byte) 0X39, "20.0", "60.0",
                "V", "0.1", 0, 1, 7, isSetting(true), ""));//请求充电电压
        item.childs.add(getDeviceParam(R.string.charge_rate, (byte) 0x02, (byte) 0X32, "0", "1",
                "C", "0.001", 0, 3, 0, isSetting(true), ""));//充电倍率
        item.childs.add(getDeviceParam(R.string.discharge_rate, (byte) 0x02, (byte) 0X33, "0", "1",
                "C", "0.001", 0, 3, 1, isSetting(true), ""));//放电倍率
        list.add(item);

        childParams1 = new int[]{0, 1};
        childParams2 = new int[]{2};
        return list;
    }


    @Override
    protected String getTitleMsg() {
        return getString(R.string.function_parameters);
    }

    /**
     * 启动页面
     **/
    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, FunctionParamActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }

    /**
     * 收到参数后刷新界面
     */
    public void refreshParam(int commandType, byte[] receiveArray) {
        Log.e("111", "收到实时数据refreshParam");
        if (commandType == CommandData.command_type_function_param1 || commandType == CommandData.command_type_function_param2) {
            ParentParam parentParam = mCommonParamAdapter.getItem(0);
            DeviceParam item;
            if (parentParam.childs != null && !parentParam.childs.isEmpty()) {
                if (commandType == CommandData.command_type_function_param1) {
                    for (int i : childParams1) {
                        item = parentParam.childs.get(i);
                        if (item.position >= 0) {
                            item.readHexVal = ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[item.position * 2], receiveArray[item.position * 2 + 1]});
                            item.convertVal = item.convertHexToVal();
                        }
                    }

                } else {
                    for (int i : childParams2) {
                        item = parentParam.childs.get(i);
                        if (item.position >= 0) {
                            item.readHexVal = ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[item.position * 2], receiveArray[item.position * 2 + 1]});
                            item.convertVal = item.convertHexToVal();
                        }
                    }
                }
                mCommonParamAdapter.notifyItemRangeChanged(0, 1);
            }
        }
        //自动休眠时间
        if (commandType == CommandData.command_type_function_param3) {
            getParamHexVal(1, receiveArray);
            mCommonParamAdapter.notifyItemRangeChanged(1, 1);
        }
        //mos开关和强控
        if (commandType == CommandData.command_type_function_param4) {
            mosStatus = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[0], receiveArray[1]}), 16);
            isStorageControl = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[2], receiveArray[3]}), 16) == 1;
            isStorageControl = Long.parseLong(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[2], receiveArray[3]}), 16) == 1;
            ParentParam parentParam = mCommonParamAdapter.getItem(2);
            parentParam.isStorageControl = isStorageControl;
            mCommonParamAdapter.notifyItemRangeChanged(2, 1);
        }
        //充放电管理
        if (commandType == CommandData.command_type_function_param5) {
            getParamHexVal(3, receiveArray);
            mCommonParamAdapter.notifyItemRangeChanged(3, 1);
        }
    }

    /**
     * 监听电池组实时数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowBtgRealtimeDataEvent(EventManager.OnShowBtgRealtimeDataEvent event) {
        Log.e("111", "收到实时数据");
        MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_function_param1, (byte) 0x00, (byte) 0xA9, 2, "查询功能参数1", false);
        MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_function_param2, (byte) 0x00, (byte) 0xCF, 1, "查询功能参数2", false);
        MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_function_param4, (byte) 0x00, (byte) 0xCA, 2, "查询功能参数4", false);
        MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_function_param5, (byte) 0x02, (byte) 0x32, 8, "查询功能参数5", false);
        // 获取电池组数据
        BTGRealtimeData btgRealtimeData = event.getBTGRealtimeData();
        ParentParam parentParam = mCommonParamAdapter.getItem(2);
        parentParam.childs.get(0).checked = btgRealtimeData.chgMos == 1;
        parentParam.childs.get(1).checked = btgRealtimeData.disChgMos == 1;
        parentParam.childs.get(2).checked = btgRealtimeData.disChgCurrent == 1;
        parentParam.childs.get(3).checked = btgRealtimeData.heatingFilm == 1;
        mCommonParamAdapter.notifyItemRangeChanged(2, 1);
    }

    public static void main(String[] args) {
        int a = 0x8000 + 500;
        System.out.println("aa=" + a);
    }

}
