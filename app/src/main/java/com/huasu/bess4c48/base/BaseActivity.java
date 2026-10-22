package com.huasu.bess4c48.base;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.ui.LauncherActivity;
import com.huasu.bess4c48.utils.LanguageUtil;
import com.huasu.bess4c48.utils.ScreenFullUtils;
import com.huasu.bess4c48.utils.ToolUtil;


public abstract class BaseActivity extends AppCompatActivity {
    public AppCompatActivity mActivity;
    public Context mContext;
    private static final String TAG = "BaseActivity";
    protected RelativeLayout mRlTitleBar;
    protected TextView mTvTitle; //公用的title控件
    protected ImageView mIvBack;
    protected View mVStatusBg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "当前页面:" + getClass().getSimpleName());
        super.onCreate(savedInstanceState);
        LanguageUtil.initAppLanguage(this);
//        setImmersiveByInsets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ScreenFullUtils.fixNavigationBarOverlay(this);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams _localLayoutParams = getWindow().getAttributes();
            _localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | _localLayoutParams.flags);
        }
        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID(), isTitleBar());
            mActivity = this;
            mContext = this;
            MyApplication.getInstance().setActivity(this);
            ToolUtil.initDisplayMetrics(mActivity);
            initView();
        } else {
            finish();
        }
        hideActionBar();

    }

    public void setImmersiveByInsets() {
        Window window = getWindow();
        View decorView = window.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ 最新API
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setDecorFitsSystemWindows(false);
            window.getInsetsController().hide(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
            window.getInsetsController().setSystemBarsBehavior(android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 兼容API
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(flags);
            window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
            window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        }
    }


    public void setContentView(int layoutResID, boolean isTitleBar) {
        if (!isTitleBar) {
            setContentView(layoutResID);
            return;
        }
        RelativeLayout baseLayout = new RelativeLayout(this);
        baseLayout.setBackgroundColor(Color.TRANSPARENT);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 添加头部布局
        View titleBar = inflater.inflate(R.layout.layout_basic_titlebar, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.toolbar_height));
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        baseLayout.addView(titleBar, params);
        // 添加内容布局
        params = new RelativeLayout.LayoutParams(-1, -1);
        View contentView = inflater.inflate(layoutResID, null);
        params.addRule(RelativeLayout.BELOW, R.id.rlTitleBar);
        baseLayout.addView(contentView, params);

        RelativeLayout.LayoutParams paramsLine = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        paramsLine.addRule(RelativeLayout.BELOW, R.id.rlTitleBar);
        setContentView(baseLayout);


    }

    protected void initView() {
        mRlTitleBar = findViewById(R.id.rlTitleBar);
        mTvTitle = findViewById(R.id.tvTitle);
        mIvBack = findViewById(R.id.ivBack);
        mVStatusBg = findViewById(R.id.vStatusBg);
        if (mVStatusBg != null && !(mActivity instanceof LauncherActivity)) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ToolUtil.getStatusBarHeight(mContext));
            mVStatusBg.setLayoutParams(params);
        }

        if (mIvBack != null) {
            mIvBack.setOnClickListener(new OnClickListen());
        }
        if (mTvTitle != null) {
            mTvTitle.setText(getTitleMsg());
        }

    }

    /**
     * 获取页面id
     *
     * @return
     */
    protected abstract int getContentViewLayoutID();

    /**
     * 是否添加title
     *
     * @return
     */
    protected abstract boolean isTitleBar();

    /**
     * 设置标题栏标题
     *
     * @return
     */
    protected abstract String getTitleMsg();

    public boolean isBluetoothConnected(boolean showToast) {
        if (MyApplication.getInstance().isConnected)
            return true;
        if (showToast)
            MyApplication.showFailedDialog(mActivity, getString(R.string.bluetooth_connect_tips));
        return false;
    }

    public class OnClickListen implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivBack:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }


    public void hideActionBar() {
        //TODO 隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


    public void refreshParam(int commandType, byte[] receiveArray) {
    }


    protected boolean isCurrentActivity() {
        return mActivity == MyApplication.getInstance().getCurrentActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.mApplication.removeActivity(this);
    }


}

