package com.huasu.bess4c48.base;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.adapter.CommonParamAdapter;
import com.huasu.bess4c48.bean.CommandData;
import com.huasu.bess4c48.bean.DeviceParam;
import com.huasu.bess4c48.bean.ParentParam;
import com.huasu.bess4c48.callback.MyUiCallback;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.ByteUtils;
import com.huasu.bess4c48.utils.ToolUtil;
import com.huasu.bess4c48.view.dialog.BaseToastDialog;
import com.huasu.bess4c48.view.dialog.InputParamDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.List;

public abstract class BaseSettingParamActivity extends BaseActivity {

    protected RecyclerView rvInfo;
    protected CommonParamAdapter mCommonParamAdapter;
    @Nullable
    protected Button btnRead, btnRestore;
    private InputParamDialog mInputParamDialog;

    public void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        btnRead = findViewById(R.id.btnRead);
        btnRestore = findViewById(R.id.btnRestore);
        ToolUtil.inspectionAuthority(btnRestore, View.GONE);
        rvInfo = findViewById(R.id.rvInfo);
        rvInfo.setItemAnimator(null);
        mCommonParamAdapter = new CommonParamAdapter(init(), new CommonParamAdapter.OnClick() {
            @Override
            public void onClick(int position) {
                onParentItemClick(position);
            }

            @Override
            public void onChildClick(int parentPosition, int childPosition, DeviceParam child, String parentName) {
                if (child.isSetting())
                    onChildItemClick(parentPosition, childPosition, child, parentName);
            }
        });
        rvInfo.setLayoutManager(new LinearLayoutManager(mContext));
        rvInfo.setAdapter(mCommonParamAdapter);
        if (btnRestore != null)
            btnRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });

        if (btnRead != null)
            btnRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readParam(true);
                }
            });
        readParam(false);
    }

    //读取参数
    public abstract void readParam(boolean showToast);

    public void readParam(boolean showToast, int position) {
    }

    //点击parent item
    protected void onParentItemClick(int position) {
        readParam(true, position);
    }

    //点击child item
    protected void onChildItemClick(int parentPosition, int childPosition, DeviceParam child, String parentName) {
        showInputValDialog(parentPosition, childPosition, child, parentName);
    }

    protected void showInputValDialog(final int parentPosition, final int childPosition, final DeviceParam child, String parentName) {
        mInputParamDialog = InputParamDialog.newInstance(child, parentName,
                new InputParamDialog.OnViewClickListener() {

                    @Override
                    public void dismiss() {
                        mInputParamDialog = null;
                    }


                    @Override
                    public void onSure(String inputVal) {
                        writeParam(parentPosition, childPosition, child, inputVal);
                        dismiss();
                    }
                });
        mInputParamDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), mContext.getClass().getSimpleName());
    }

    protected void writeParam(int parentPosition, int childPosition, DeviceParam child, String inputVal) {
        if (isBluetoothConnected(true)) {
            try {
                BigDecimal data = new BigDecimal(inputVal).divide(new BigDecimal(child.zoomStr));
                data = data.add(new BigDecimal(child.offsetVal));
                if (data.compareTo(BigDecimal.ZERO) >= 0 && (data.scale() <= 0 || data.stripTrailingZeros().scale() <= 0)) {
                    MyUiCallback.getInstance().sendWriteCommand(CommandData.command_type_setting, child.highAddr, child.lowAddr, getAddVal() + data.intValue(), "设置" + child.paramName, true, parentPosition, childPosition);
                } else {
                    MyApplication.showToastSuccess(getString(R.string.finish_setting_params_failed));
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyApplication.showToastSuccess(getString(R.string.finish_setting_params_failed));

            }

        }
    }

    public int getAddVal() {
        return 0;
    }

    public abstract List<ParentParam> init();


    /**
     * @param strResId
     * @param highAddr
     * @param lowAddr
     * @param minVal
     * @param maxVal
     * @param paramUnit
     * @param zoomStr   缩放
     * @param offsetVal 偏移
     * @param precision 精度，保留几个小数点
     * @param position
     * @param setting
     * @param resStr
     * @return
     */
    public DeviceParam getDeviceParam(int strResId, byte highAddr, byte lowAddr, String minVal, String maxVal,
                                      String paramUnit, String zoomStr, int offsetVal, int precision, int position, int setting, String resStr) {
        DeviceParam child = new DeviceParam();
        child.paramName = getString(strResId) + (TextUtils.isEmpty(paramUnit) ? "" : "(" + paramUnit + ")");
        child.highAddr = highAddr;
        child.lowAddr = lowAddr;
        child.minVal = minVal;
        child.maxVal = maxVal;
        child.paramUnit = paramUnit;
        child.zoomStr = zoomStr;
        child.precision = precision;
        child.position = position;
        child.offsetVal = offsetVal;
        child.resStr = resStr;
        child.setting = setting;
        return child;
    }

    /**
     * @param strResId
     * @param highAddr
     * @param lowAddr
     * @param minVal
     * @param maxVal
     * @param paramUnit
     * @param zoomStr   缩放
     * @param offsetVal 偏移
     * @param precision 精度，保留几个小数点
     * @param position
     * @param setting
     * @param resStr
     * @return
     */
    public DeviceParam getDeviceParam(int strResId, byte highAddr, byte lowAddr, String minVal, String maxVal,
                                      String paramUnit, String zoomStr, int offsetVal, int precision, int position, int setting, String resStr, int itemType) {
        DeviceParam child = new DeviceParam();
        child.paramName = getString(strResId) + (TextUtils.isEmpty(paramUnit) ? "" : "(" + paramUnit + ")");
        child.highAddr = highAddr;
        child.lowAddr = lowAddr;
        child.minVal = minVal;
        child.maxVal = maxVal;
        child.paramUnit = paramUnit;
        if (TextUtils.isEmpty(zoomStr))
            child.zoomStr = "1";
        else
            child.zoomStr = zoomStr;
        child.precision = precision;
        child.position = position;
        child.offsetVal = offsetVal;
        child.resStr = resStr;
        child.setting = setting;
        child.itemType = itemType;
        return child;
    }


    /**
     * @param imgResId
     * @param strResId
     * @param highAddr
     * @param lowAddr
     * @param minVal
     * @param maxVal
     * @param paramUnit
     * @param zoomStr   缩放
     * @param offsetVal 偏移
     * @param precision 精度，保留几个小数点
     * @param position
     * @param setting
     * @param resStr
     * @return
     */
    public DeviceParam getDeviceParam(int imgResId, int strResId, byte highAddr, byte lowAddr, String minVal, String maxVal,
                                      String paramUnit, String zoomStr, int offsetVal, int precision, int position,
                                      int setting, String resStr) {
        DeviceParam child = new DeviceParam();
        child.imgResId = imgResId;
        child.paramName = getString(strResId) + (TextUtils.isEmpty(paramUnit) ? "" : "(" + paramUnit + ")");
        child.highAddr = highAddr;
        child.lowAddr = lowAddr;
        child.minVal = minVal;
        child.maxVal = maxVal;
        child.paramUnit = paramUnit;
        child.zoomStr = zoomStr;
        child.precision = precision;
        child.position = position;
        child.offsetVal = offsetVal;
        child.resStr = resStr;
        child.setting = setting;
        return child;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_common_param;
    }

    @Override
    protected boolean isTitleBar() {
        return true;
    }

    public void getParamHexVal(int position, byte[] receiveArray) {
        try {
            ParentParam parentParam = mCommonParamAdapter.getItem(position);
            if (parentParam.childs != null && !parentParam.childs.isEmpty()) {
                for (DeviceParam item : parentParam.childs) {
                    if (item.position >= 0) {
                        item.readHexVal = ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[item.position * 2], receiveArray[item.position * 2 + 1]});
                        item.convertVal = item.convertHexToVal();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int isSetting(boolean isSetting) {
        if (isSetting && MyApplication.getInstance().isSysAdmin())
            return 1;
        return 0;
    }

    /**
     * 收到读取参数的返回值
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowDeviceParamEvent(EventManager.OnShowDeviceParamEvent event) {
        if (event.getReceiveArray() != null && isCurrentActivity()) {
            if (event.isShowToast())
                MyApplication.showToastSuccess(getString(R.string.finish_reading_params));
            refreshParam(event.getCommandType(), event.getReceiveArray());
        }
    }

    /**
     * 收到设置完参数的返回，设置成功需要发送一次读取指定
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowSettingDeviceParamEvent(EventManager.OnShowSettingDeviceParamEvent event) {
        if (event.isShowToast() && isCurrentActivity())
            MyApplication.showToastSuccess(getString(event.isSuccess() ? R.string.finish_setting_params_successed : R.string.finish_setting_params_failed));
        //如果成功了，需要再读取一次当前寄存器的值
      /*  if (event.getChildPosition() >= 0 && event.getParentPosition() >= 0) {
            DeviceParam child = mCommonParamAdapter.getChildItem(event.getParentPosition(), event.getChildPosition());
            MyUiCallbackNew.getInstance().sendReadCommand(CommandData.command_type_reading, child.highAddr, child.lowAddr, 1, "查询" + child.paramName, event.getParentPosition(), event.getChildPosition());
        }*/
        if(event.isSuccess()){
            readParam(false);
        }
    }

    /**
     * 收到设置完参数后又下发读取参数的返回值
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowReadOneDeviceParamEvent(EventManager.OnShowReadOneDeviceParamEvent event) {
        if (event.getReceiveArray() != null && isCurrentActivity()) {
            try {
                Log.e("收到：", "内容=" + ByteUtils.bytesToHexString(event.getReceiveArray()));
                //如果成功了，需要再读取一次当前寄存器的值
                DeviceParam child = mCommonParamAdapter.getChildItem(event.getParentPosition(), event.getChildPosition());
                child.readHexVal = ByteUtils.byteArrTo2Hex(new byte[]{event.getReceiveArray()[0], event.getReceiveArray()[1]});
                child.convertVal = child.convertHexToVal();
                mCommonParamAdapter.notifyItemRangeChanged(event.getParentPosition(), 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 收到恢复默认值返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnRestoreDeviceParamEvent(EventManager.OnRestoreDeviceParamEvent event) {
        if (event.isShowToast() && isCurrentActivity())
            MyApplication.showToastSuccess(getString(event.isSuccess() ? R.string.finish_restore_params_successed : R.string.finish_restore_params_failed));
        //恢复完参数后再读取一遍参数
        readParam(false);
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
                        MyUiCallback.getInstance().sendRestoreCommand(CommandData.command_type_restore, (byte) 0x00, (byte) 0xBE, 61440, "恢复默认值", true);
                    }
                }, getString(R.string.restore_param_tips));
        mBaseToastDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), mContext.getClass().getSimpleName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
