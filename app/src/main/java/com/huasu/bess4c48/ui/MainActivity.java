package com.huasu.bess4c48.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseActivity;
import com.huasu.bess4c48.bean.TabEntity;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.ui.home.HomeFragment;
import com.huasu.bess4c48.ui.paramSetting.ParamSettingFragment;
import com.huasu.bess4c48.ui.realtimeAlarm.RealTimeAlarmFragment;
import com.huasu.bess4c48.ui.realtimeData.RealTimeDataFragment;
import com.huasu.bess4c48.ui.sysSetting.SysSettingActivity;
import com.huasu.bess4c48.utils.BleHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private int[] mTitlesResId = {
            R.string.menu_home_short, R.string.menu_realtime_data_short,
            R.string.menu_realtime_alarm_short, R.string.menu_param_setting_short,
    };

    private int[] mIconUnselectIds = {
            R.mipmap.ic_menu_home_unselected, R.mipmap.ic_menu_reatime_data_unselected,
            R.mipmap.ic_menu_realtime_alarm_unselected, R.mipmap.ic_menu_param_setting_unselected};

    private int[] mIconSelectIds = {
            R.mipmap.ic_menu_home_selected, R.mipmap.ic_menu_realtime_data_selected,
            R.mipmap.ic_menu_realtime_alarm_selected, R.mipmap.ic_menu_param_setting_selected};


    private CommonTabLayout mCommonTabLayout;

    private HomeFragment homeFragment;
    private RealTimeDataFragment realTimeDataFragment;
    private RealTimeAlarmFragment realTimeAlarmFragment;
    private ParamSettingFragment paramSettingFragment;


    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();


    private IntentFilter intentFilter;

    private TextView tvTitleText;
    private ImageView ivTitleIcon;
    private ImageView ivSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initTab();
        initFragment(savedInstanceState);
        showGuidePage();
        // initBroadcastReceiver();

        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isTitleBar() {
        return false;
    }

    @Override
    protected String getTitleMsg() {
        return null;
    }

    /**
     * 初始化底部菜单栏
     */
    public void initTab() {

        tvTitleText = findViewById(R.id.tvTitleText);
        ivTitleIcon = findViewById(R.id.ivTitleIcon);
        ivSetting = findViewById(R.id.ivSetting);

        for (int i = 0; i < mTitlesResId.length; i++) {
            mTabEntities.add(new TabEntity(mTitlesResId[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        mCommonTabLayout = findViewById(R.id.commonTabLayout);
        mCommonTabLayout.setTabData(mTabEntities);
        mCommonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mCommonTabLayout.setCurrentTab(position);
                switchFragment(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysSettingActivity.launchActivity();
            }
        });
    }


    /**
     * 显示引导页
     **/
    public void showGuidePage() {

        int layoutId = R.layout.dialog_guide;
        Locale locale = getResources().getConfiguration().locale;
        Log.d(TAG, "showGuidePage: locale: " + locale.getLanguage());
        if (locale.getLanguage().equals("en")) {
            layoutId = R.layout.dialog_guide_en;
        }
        NewbieGuide.with(this)
                .alwaysShow(false)
                .setLabel("guide")
                .addGuidePage(
                        GuidePage.newInstance()
                                .setLayoutRes(layoutId, R.id.slGuide, R.id.ivGuide)
                                .setEverywhereCancelable(false)
                )
                .show();
    }

    /**
     * 初始化Fragment
     */
    public void initFragment(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (savedInstanceState != null) {
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("homeFragment");
            realTimeDataFragment = (RealTimeDataFragment) getSupportFragmentManager().findFragmentByTag("realTimeDataFragment");
            realTimeAlarmFragment = (RealTimeAlarmFragment) getSupportFragmentManager().findFragmentByTag("realTimeAlarmFragment");
            paramSettingFragment = (ParamSettingFragment) getSupportFragmentManager().findFragmentByTag("paramSettingFragment");
            currentTabPosition = savedInstanceState.getInt("current_tab");
        } else {
            homeFragment = new HomeFragment();
            realTimeDataFragment = new RealTimeDataFragment();
            realTimeAlarmFragment = new RealTimeAlarmFragment();
            paramSettingFragment = new ParamSettingFragment();

            transaction.add(R.id.frameLayout, homeFragment, "homeFragment");
            transaction.add(R.id.frameLayout, realTimeDataFragment, "realTimeDataFragment");
            transaction.add(R.id.frameLayout, realTimeAlarmFragment, "realTimeAlarmFragment");
            transaction.add(R.id.frameLayout, paramSettingFragment, "paramSettingFragment");
        }
        transaction.commit();
        switchFragment(currentTabPosition);
        mCommonTabLayout.setCurrentTab(currentTabPosition);
    }


    /**
     * 切换Fragment
     */
    public void switchFragment(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            // 首页
            case 0:
                transaction.hide(realTimeDataFragment);
                transaction.hide(realTimeAlarmFragment);
                transaction.hide(paramSettingFragment);
                transaction.show(homeFragment);
                transaction.commitAllowingStateLoss();
                tvTitleText.setText(getString(R.string.app_name));
                break;
            // 实时数据
            case 1:
                transaction.hide(homeFragment);
                transaction.hide(realTimeAlarmFragment);
                transaction.hide(paramSettingFragment);
                transaction.show(realTimeDataFragment);
                transaction.commitAllowingStateLoss();
                tvTitleText.setText(getString(R.string.menu_realtime_data));
                break;
            // 实时告警
            case 2:
                transaction.hide(homeFragment);
                transaction.hide(realTimeDataFragment);
                transaction.hide(paramSettingFragment);
                transaction.show(realTimeAlarmFragment);
                transaction.commitAllowingStateLoss();
                tvTitleText.setText(getString(R.string.menu_realtime_alarm));
                break;
            // 参数设置
            case 3:
                transaction.hide(homeFragment);
                transaction.hide(realTimeDataFragment);
                transaction.hide(realTimeAlarmFragment);
                transaction.show(paramSettingFragment);
                transaction.commitAllowingStateLoss();
                tvTitleText.setText(getString(R.string.menu_param_setting));
                break;
            default:
                break;
        }
    }


    /**
     * 崩溃的话, 保存当前位置
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //奔溃前保存位置
        if (mCommonTabLayout != null) {
            outState.putInt("current_tab", mCommonTabLayout.getCurrentTab());
        }
    }


    /**
     * 监听蓝牙连接成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectSuccessEvent(EventManager.OnBleConnectSuccessEvent event) {
        Log.e(TAG, "蓝牙连接状态改变OnBleConnectSuccessEvent");
        ivTitleIcon.setVisibility(View.GONE);
//        MyApplication.showSuccessDialog(MainActivity.this, getString(R.string.bluetooth_connect_success));
    }


    /**
     * 监听蓝牙连接失败
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectFailedEvent(EventManager.OnBleConnectFailedEvent event) {
        Log.e(TAG, "蓝牙连接状态改变onBleConnectFailedEvent");
        ivTitleIcon.setVisibility(View.GONE);
        // MyApplication.showToastSuccess(getString(R.string.bluetooth_connect_failed));
        MyApplication.showFailedDialog(MainActivity.this, getString(R.string.bluetooth_connect_failed));
    }


    /**
     * 监听蓝牙断开
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDisconnectEvent(EventManager.OnBleDisconnectEvent event) {
        Log.e(TAG, "蓝牙连接状态改变onBleDisconnectEvent");
        ivTitleIcon.setVisibility(View.GONE);
        MyApplication.showSuccessDialog(this, getString(R.string.toast_disconnect_success));
        // MyApplication.showToastFailed(getString(R.string.toast_disconnect_success));
    }


    /**
     * 监听页面切换
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFragmentSwitchEvent(EventManager.OnFragmentSwitchEvent event) {
        int position = event.getPosition();
        mCommonTabLayout.setCurrentTab(position);
        switchFragment(position);

    }


    /**
     * 监听保护板addr地址获取不到
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectFailEvent(EventManager.OnConnectFailEvent event) {
        MyApplication.showFailedDialog(this, getString(R.string.toast_board_connect_exception));
        ivTitleIcon.setVisibility(View.VISIBLE);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowBtgRealtimeDataEvent(EventManager.OnShowBtgRealtimeDataEvent event) {
        // 收到数据的话, 取消保护板异常的指示灯
        ivTitleIcon.setVisibility(View.GONE);
    }

    /**
     * 3秒内连续按2次退出按钮, 则关闭APP
     **/
    long lastTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            if (lastTime == 0) {
                lastTime = currentTime;
                MyApplication.showToastLoading(getString(R.string.toast_press_to_exit));
                return false;
            }
            long diff = currentTime - lastTime;
            Log.d(TAG, "onKeyDown: diff: " + diff);
            if (diff < 3000) {
                System.exit(0);
                return super.onKeyDown(keyCode, event);
            } else {
                lastTime = currentTime;
                MyApplication.showToastLoading(getString(R.string.toast_press_to_exit));
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleHelper.getInstance().disconnectGatt();
        EventBus.getDefault().unregister(this);
        Log.e("onDestroy1", "onDestroy");
        // unregisterReceiver(screenReceiver);
    }

    /**
     * 启动页面
     **/
    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }
}