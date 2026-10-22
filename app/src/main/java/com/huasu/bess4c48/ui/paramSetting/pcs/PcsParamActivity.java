package com.huasu.bess4c48.ui.paramSetting.pcs;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseSettingParamActivity;
import com.huasu.bess4c48.bean.CommandData;
import com.huasu.bess4c48.bean.DeviceParam;
import com.huasu.bess4c48.bean.ParentParam;
import com.huasu.bess4c48.bean.SelectionData;
import com.huasu.bess4c48.callback.MyUiCallback;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.ByteUtils;
import com.huasu.bess4c48.utils.CPResourceUtil;
import com.huasu.bess4c48.view.dialog.SelectionOneDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class PcsParamActivity extends BaseSettingParamActivity {

    private int canPosition, rs485Position;
    private ArrayList<SelectionData> canDataArrayList, rs485DataArrayList;

    @Override
    public void initView() {
        super.initView();
        btnRestore.setVisibility(View.GONE);
    }

    //点击child item
    protected void onChildItemClick(int parentPosition, int childPosition, DeviceParam child, String parentName) {
        showSelectionDialog(childPosition == 0, childPosition, child);
    }

    public void readParam(boolean showToast) {
        if (isBluetoothConnected(showToast)) {
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_pcs_param, (byte) 0x02, (byte) 0x22, 2, "查询PCS参数", showToast);
        }
    }

    public List<ParentParam> init() {
        canDataArrayList = new ArrayList<>();
        for (int i = 0; i <= 12; i++) {
            canDataArrayList.add(new SelectionData(String.valueOf(i), CPResourceUtil.getResStringById("protocol_can_" + i)));
        }
        for (int i = 14; i <= 27; i++) {
            canDataArrayList.add(new SelectionData(String.valueOf(i), CPResourceUtil.getResStringById("protocol_can_" + i)));
        }
        rs485DataArrayList = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            rs485DataArrayList.add(new SelectionData(String.valueOf(i), CPResourceUtil.getResStringById("protocol_rs485_" + i)));
        }
        List<ParentParam> list = new ArrayList<>();
        //放电温度低
        ParentParam item = new ParentParam();
        item.title = getString(R.string.protocol_selection_label);
        item.childs.add(getDeviceParam(R.string.can_protocol_option, (byte) 0x02, (byte) 0x23, "--", "--",
                "", "1", 0, 0, 1, isSetting(true), "protocol_can_"));//CAN协议
        item.childs.add(getDeviceParam(R.string.rs485_protocol_option, (byte) 0x02, (byte) 0x22, "--", "--"
                , "", "1", 0, 0, 0, isSetting(true), "protocol_rs485_"));//RS485协议
        list.add(item);

        return list;
    }


    @Override
    protected String getTitleMsg() {
        return getString(R.string.pcs_parameters);
    }

    /**
     * 启动页面
     **/
    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, PcsParamActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }

    /**
     * 收到参数
     */
    public void refreshParam(int commandType, byte[] receiveArray) {
        if (commandType == CommandData.command_type_pcs_param) {
            getParamHexVal(0, receiveArray);
            DeviceParam child = mCommonParamAdapter.getChildItem(0, 0);
            if (!TextUtils.isEmpty(child.convertVal))
                canPosition = getCanPosition(child.convertVal);
            child = mCommonParamAdapter.getChildItem(0, 1);
            if (!TextUtils.isEmpty(child.convertVal))
                rs485Position = Integer.parseInt(child.convertVal);
            mCommonParamAdapter.notifyItemRangeChanged(0, 1);
        }
    }

    private int getCanPosition(String val) {
        if (TextUtils.isEmpty(val) || canDataArrayList == null || canDataArrayList.isEmpty())
            return 0;
        SelectionData item;
        for (int i = 0; i < canDataArrayList.size(); i++) {
            item = canDataArrayList.get(i);
            if (val.equals(item.value))
                return i;
        }
        return 0;
    }

    private SelectionOneDialog selectionOneDialog;

    private void showSelectionDialog(boolean can, final int childPosition, final DeviceParam child) {
        selectionOneDialog = SelectionOneDialog.newInstance(can ? canDataArrayList : rs485DataArrayList, child.paramName, can ? canPosition : rs485Position, new SelectionOneDialog.OnViewClickListener() {
            @Override
            public void onSelect(int checkPosition) {

            }

            @Override
            public void dismiss() {
                selectionOneDialog = null;
            }

            @Override
            public void onSure(int checkPosition, SelectionData checkedItem) {
                writeParam(0, childPosition, child, checkedItem.value);
                dismiss();

            }
        });
        selectionOneDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
    }

    /**
     * 收到设置完参数后又下发读取参数的返回值
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowReadOneDeviceParamEvent(EventManager.OnShowReadOneDeviceParamEvent event) {
        if (event.getReceiveArray() != null && isCurrentActivity()) {
            try {
                //如果成功了，需要再读取一次当前寄存器的值
                DeviceParam child = mCommonParamAdapter.getChildItem(event.getParentPosition(), event.getChildPosition());
                child.readHexVal = ByteUtils.byteArrTo2Hex(new byte[]{event.getReceiveArray()[0], event.getReceiveArray()[1]});
                child.convertVal = child.convertHexToVal();
                if (!TextUtils.isEmpty(child.convertVal))
                    if (event.getChildPosition() == 0)
                        canPosition = getCanPosition(child.convertVal);
                    else
                        rs485Position = Integer.parseInt(child.convertVal);
                mCommonParamAdapter.notifyItemRangeChanged(event.getParentPosition(), 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
