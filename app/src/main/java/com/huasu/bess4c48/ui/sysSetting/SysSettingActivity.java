package com.huasu.bess4c48.ui.sysSetting;

import android.content.Intent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.adapter.BaseRecyclerViewHolder;
import com.huasu.bess4c48.adapter.SettingAdapter;
import com.huasu.bess4c48.base.BaseActivity;
import com.huasu.bess4c48.bean.MyDevice;
import com.huasu.bess4c48.bean.SelectionData;
import com.huasu.bess4c48.bean.SettingItem;
import com.huasu.bess4c48.ui.LauncherActivity;
import com.huasu.bess4c48.ui.sysSetting.deviceManage.DeviceManageActivity;
import com.huasu.bess4c48.ui.sysSetting.historyData.HistoryDataExportActivity;
import com.huasu.bess4c48.ui.paramSetting.bat.BatParamActivity;
import com.huasu.bess4c48.ui.paramSetting.function.FunctionParamActivity;
import com.huasu.bess4c48.ui.paramSetting.pcs.PcsParamActivity;
import com.huasu.bess4c48.ui.sysSetting.upgrade.UpgradeActivity;
import com.huasu.bess4c48.utils.BleHelper;
import com.huasu.bess4c48.utils.LanguageUtil;
import com.huasu.bess4c48.view.dialog.SelectionOneDialog;

import java.util.ArrayList;
import java.util.List;

public class SysSettingActivity extends BaseActivity {
    private RecyclerView rvList;
    private ArrayList<SelectionData> selectionDataArrayList = new ArrayList<>();

    @Override
    protected void initView() {
        super.initView();
        selectionDataArrayList.add(new SelectionData("en", getString(R.string.setting_language_en)));
        selectionDataArrayList.add(new SelectionData("zh-rCN", getString(R.string.setting_language_cn)));
        rvList = findViewById(R.id.rvList);
        SettingAdapter adapter = new SettingAdapter(init());
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        rvList.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyclerViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, int position) {
                switch (position) {
                   /* case 0:
                        changeLanguage();
                        break;*/
                    case 0:
                        DeviceManageActivity.launchActivity();
                        break;
                    case 1:
                        UpgradeActivity.launchActivity();
                        break;
                    case 2:
                        HistoryDataExportActivity.launchActivity();
                        break;
                }
            }
        });
    }

    private List<SettingItem> init() {
        List<SettingItem> list = new ArrayList<>();
//        list.add(new SettingItem(R.mipmap.icon_language, R.string.setting_language_switch));
        list.add(new SettingItem(R.mipmap.icon_device_manage, R.string.setting_device_management));
        list.add(new SettingItem(R.mipmap.icon_upgrade, R.string.setting_upgrade_management));
        list.add(new SettingItem(R.mipmap.icon_data_export, R.string.setting_data_export));
//        list.add(new SettingItem(R.mipmap.icon_bat_knowledge, R.string.setting_battery_knowledge));
//        list.add(new SettingItem(R.mipmap.icon_fault_search, R.string.setting_fault_inquiry));
        return list;
    }

    private SelectionOneDialog selectionOneDialog;

    private void changeLanguage() {
        selectionOneDialog = SelectionOneDialog.newInstance(selectionDataArrayList, getString(R.string.setting_language_select), -1, new SelectionOneDialog.OnViewClickListener() {
            @Override
            public void onSelect(int checkPosition) {

            }

            @Override
            public void dismiss() {
                selectionOneDialog = null;
            }

            @Override
            public void onSure(int checkPosition, SelectionData checkedItem) {
                dismiss();
                if (checkedItem != null) {
                    LanguageUtil.setAppLanguage(mContext, checkedItem.value);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LanguageUtil.restartActivity(mContext, LauncherActivity.class);
                }

            }
        });
        selectionOneDialog.show(getSupportFragmentManager(), getClass().getSimpleName());

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_param_setting;
    }

    @Override
    protected boolean isTitleBar() {
        return true;
    }

    @Override
    protected String getTitleMsg() {
        return getString(R.string.menu_system_setting);
    }

    /**
     * 启动页面
     **/
    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, SysSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }

}
