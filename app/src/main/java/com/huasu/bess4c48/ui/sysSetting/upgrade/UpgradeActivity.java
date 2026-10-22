package com.huasu.bess4c48.ui.sysSetting.upgrade;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseSettingParamActivity;
import com.huasu.bess4c48.bean.CommandData;
import com.huasu.bess4c48.bean.DeviceParam;
import com.huasu.bess4c48.bean.ParentParam;
import com.huasu.bess4c48.callback.MyUiCallback;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.ByteUtils;
import com.huasu.bess4c48.utils.FileUtils;
import com.huasu.bess4c48.utils.InstructionUtil;
import com.huasu.bess4c48.view.dialog.BaseToastDialog;
import com.huasu.bess4c48.view.dialog.UpgradeDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UpgradeActivity extends BaseSettingParamActivity {
    public TextView tvFile;
    public Button btnSelect, bUpgrade;
    public byte[] fileBytes;//升级文件转成的字节数组
    public String fileName;//升级文件名
    public int packageIndex = 0;//传递到第几包字节
    public int packageLength;//每次下发多少字节
    public long fileSize;//升级文件大小

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_upgrade;
    }

    @Override
    public void initView() {
        super.initView();
        btnRead.setVisibility(View.GONE);
        btnRestore.setVisibility(View.GONE);
        tvFile = findViewById(R.id.tvFile);
        btnSelect = findViewById(R.id.btnSelect);
        bUpgrade = findViewById(R.id.bUpgrade);
        bUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileBytes == null || fileSize == 0) {
                    MyApplication.showToastFailed(getString(R.string.select_upgrade_file));
                    return;
                }
                if (isBluetoothConnected(true)) {
                    //下发启动指令
                    showConfirmDialog();
                }
            }
        });
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                someActivityResultLauncher.launch(intent); // 旧的方法，不推荐使用，改用下面的方式
            }
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri fileUri = result.getData().getData();
                        if (fileUri != null) {
                            String filePath = FileUtils.getPath(mContext, fileUri);
                            if (!TextUtils.isEmpty(filePath)) {
                                File file = new File(filePath);
                                fileSize = file.length();
                                fileName = file.getName();
                                tvFile.setText(fileName);
                                // 直接读取文件流，无需依赖绝对路径（推荐）
                                try {
                                    InputStream inputStream = getContentResolver().openInputStream(fileUri);
                                    if (inputStream != null) {
                                        fileBytes = FileUtils.inputStreamToBytes(inputStream);
                                        // 读取文件内容（如转字节数组、复制到应用私有目录等）
                                        inputStream.close();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            System.out.println("选中文件路径：" + filePath);
                        }


                    }
                }
            }
    );

    BaseToastDialog mBaseToastDialog;

    private void showConfirmDialog() {
        if (mBaseToastDialog != null)
            mBaseToastDialog.dismiss();
        mBaseToastDialog = BaseToastDialog.newInstance(new BaseToastDialog.OnViewClickListener() {
            @Override
            public void dismiss() {
                mBaseToastDialog = null;
            }

            @Override
            public void submit() {
                dismiss();
                packageIndex = 0;
                MyUiCallback.getInstance().sendStartUpgrade();
                showUpgradeDialog();
            }
        }, getString(R.string.upgrade_tips));
        mBaseToastDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
    }

    UpgradeDialog mUpgradeDialog;

    private void showUpgradeDialog() {
        if (mUpgradeDialog != null)
            mUpgradeDialog.dismiss();
        mUpgradeDialog = UpgradeDialog.newInstance();
        mUpgradeDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
    }

    @Override
    public void readParam(boolean showToast) {
        if (isBluetoothConnected(showToast)) {
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_upgrade_param1, (byte) 0x00, (byte) 0x05, 8, "查询设备程序版本号和Boot", showToast);
            MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_upgrade_param2, (byte) 0x00, (byte) 0x13, 1, "查询设备通信地址", false);
        }
    }

    public void readParam(boolean showToast, int position) {
        readParam(showToast);
    }

    @Override
    public List<ParentParam> init() {
        List<ParentParam> list = new ArrayList<>();
        //放电过流
        ParentParam item = new ParentParam();
        item.isShowReadBtn = true;
        item.title = getString(R.string.version_info_title);
        item.childs.add(getDeviceParam(R.string.program_version_title, (byte) 0x00, (byte) 0x06, "--", "--",
                "", "1", 0, 0, 1, isSetting(false), ""));//程序版本 0X06 000B 000C
        item.childs.add(getDeviceParam(R.string.boot_version_title, (byte) 0x00, (byte) 0x05, "--", "--",
                "", "1", 0, 0, 0, isSetting(false), ""));//BOOT版本
        item.childs.add(getDeviceParam(R.string.bms_communication_address_title, (byte) 0x00, (byte) 0x13, "--", "--",
                "", "1", 0, 0, 0, isSetting(false), ""));//通信地址
        list.add(item);

        return list;
    }

    /**
     * 收到参数
     */
    public void refreshParam(int commandType, byte[] receiveArray) {
        if (commandType == CommandData.command_type_upgrade_param1) {
            try {
                ParentParam parentParam = mCommonParamAdapter.getItem(0);
                if (parentParam.childs != null && !parentParam.childs.isEmpty()) {
                    DeviceParam item = parentParam.childs.get(0);
                    int v1 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[2], receiveArray[3]}), 16);
                    int v2 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[12], receiveArray[13]}), 16);
                    int v3 = Integer.parseInt(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[14], receiveArray[15]}), 16);
                    item.convertVal = v1 + "." + v2 + "." + v3;

                    item = parentParam.childs.get(1);
                    item.readHexVal = ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[item.position * 2], receiveArray[item.position * 2 + 1]});
                    item.convertVal = item.convertHexToVal();

                    mCommonParamAdapter.notifyItemRangeChanged(0, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (commandType == CommandData.command_type_upgrade_param2) {
            try {
                ParentParam parentParam = mCommonParamAdapter.getItem(0);
                if (parentParam.childs != null && !parentParam.childs.isEmpty()) {
                    DeviceParam item = parentParam.childs.get(2);
                    item.readHexVal = ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[item.position * 2], receiveArray[item.position * 2 + 1]});
                    item.convertVal = item.convertHexToVal();
                    mCommonParamAdapter.notifyItemRangeChanged(0, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //升级失败，停止升级
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnSendUpgradeFail(EventManager.OnSendUpgradeFail event) {
        MyApplication.showToastFailed(getString(event.getReason()));
        if (mUpgradeDialog != null && mUpgradeDialog.isVisible())
            mUpgradeDialog.showExportFile(getString(event.getReason()));
    }

    //启动指令下发成功，开始发送升级包消息
    @Subscribe()
    public void OnSendUpgradePackageStart(EventManager.OnSendUpgradePackageStart event) {
        //发送升级包消息
        MyUiCallback.getInstance().sendUpgradePackage(fileName, fileSize);
    }

    //升级包指令下发成功，开始发送升级消息
    @Subscribe()
    public void OnSendStartUpgrade(EventManager.OnSendStartUpgrade event) {
        //发送升级消息
        packageLength = 64;
        if (event.getWord() == 4)//64
            packageLength = 64;
        if (event.getWord() == 3)
            packageLength = 512;
        if (event.getWord() == 2)
            packageLength = 256;
        if (event.getWord() == 1)
            packageLength = 128;
        MyUiCallback.getInstance().sendUpgradeMsg(packageIndex, splitBytes());
    }

    //收到的升级过程消息
    @Subscribe()
    public void OnReceiveUpgradeProcessMsg(EventManager.OnReceiveUpgradeProcessMsg event) {
        //消息码
        //0、终端校验升级包通过回复继续下发；
        //1、校验不通过回复重发指令；
        //2、超过失败次数，回复停止下载；其他保留。
        int msgCode = event.getMsgCode();
        if (msgCode == 2) {
            MyApplication.showToastFailed(getString(R.string.upgrade_fail_for_time));
            if (mUpgradeDialog != null && mUpgradeDialog.isVisible())
                mUpgradeDialog.showExportFile(getString(R.string.upgrade_fail_for_time));
        } else if (msgCode == 0) {//发送下一包
            packageIndex++;
            byte[] sendBytes = splitBytes();
            if (sendBytes != null)//如果还有发送下一包升级，否则发送查询升级结果指令
                MyUiCallback.getInstance().sendUpgradeMsg(packageIndex, splitBytes());
            else
                MyUiCallback.getInstance().sendUpgradeResult(packageIndex);

        } else if (msgCode == 1) {//重发
            MyUiCallback.getInstance().sendUpgradeMsg(packageIndex, splitBytes());
        }
    }

    //收到的升级结果消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnReceiveUpgradeResult(EventManager.OnReceiveUpgradeResult event) {
//        MyApplication.showToastFailed(getString(event.isResult() ? R.string.upgrade_success : R.string.upgrade_fail));
        if (mUpgradeDialog != null && mUpgradeDialog.isVisible())
            mUpgradeDialog.showExportFile(getString(event.isResult() ? R.string.upgrade_success : R.string.upgrade_fail));
    }

    public byte[] splitBytes() {
        int startIndex = packageIndex * packageLength;
        if (startIndex >= fileSize) {
            return null;
        }

        // 2. 计算有效截取长度（避免超出数组末尾）
        long validLength = Math.min(packageLength, fileSize - startIndex);

        // 3. 截取子数组
        byte[] result = new byte[(int) validLength];
        System.arraycopy(
                fileBytes,  // 源数组
                startIndex,   // 源数组起始位置
                result,       // 目标数组
                0,            // 目标数组起始位置
                (int) validLength   // 拷贝长度
        );

        return result;
    }

    public static byte[] splitBytes(byte[] fileBytes, int packageIndex, int packageLength, int fileSize) {
        int startIndex = packageIndex * packageLength;
        if (startIndex >= fileSize) {
            return null;
        }
        // 2. 计算有效截取长度（避免超出数组末尾）
        long validLength = Math.min(packageLength, fileSize - startIndex);
        // 3. 截取子数组
        byte[] result = new byte[(int) validLength];
        System.arraycopy(
                fileBytes,  // 源数组
                startIndex,   // 源数组起始位置
                result,       // 目标数组
                0,            // 目标数组起始位置
                (int) validLength   // 拷贝长度
        );
        return result;
    }

    public static void main(String[] args) {
        byte[] queryCommand = new byte[]{(byte) 0xAA, 0x55, (byte) 0x80, 0x11, 0x00, 0x00, 0x00, 0x1D, 0x00, 0x03,
                0x2B, 0x18, 0x18, 0x48, 0x34, 0x38, 0x2D, 0x31, 0x30, 0x30, 0x43, 0x4C, 0x31, 0x30
                , 0x2D, 0x53, 0x30, 0x34, 0x2D, 0x30, 0x78, 0x66, 0x66, 0x2E, 0x62, 0x69, 0x6E};//TODO

        byte[] newB = InstructionUtil.packageInstruction(queryCommand, true);
        System.out.println(ByteUtils.bytesToHexString(newB));
    }

    @Override
    protected String getTitleMsg() {
        return getString(R.string.setting_upgrade_management);
    }

    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, UpgradeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }


}
