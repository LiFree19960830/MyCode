package com.huasu.bess4c48;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.res.Configuration;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.huasu.bess4c48.bean.UserInfo;
import com.huasu.bess4c48.callback.BleCallback;
import com.huasu.bess4c48.exp.CrashHandler;
import com.huasu.bess4c48.utils.LanguageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class MyApplication extends Application {
    public static MyApplication mApplication;
    public String currentAddress = "01";//当前选中的保护板地址
    public static Activity mActivity;

    public static boolean isConnected = false;

    public static Dialog dialog;
    public static UserInfo userInfo = new UserInfo();
    public List<Activity> oList;//用于存放所有启动的Activity的集合

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        // 初始化应用语言（优先使用保存的偏好，不跟随系统）
        LanguageUtil.initAppLanguage(this);
        // ToastUtils.init(this);
        initToasty();
        CrashHandler.getInstance().init(mApplication);
        oList = new ArrayList<>();
    }

    // 适配Android 7.0+，防止系统重启后语言还原
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 重新初始化语言，覆盖系统配置
        LanguageUtil.initAppLanguage(this);
    }

    public static MyApplication getInstance() {
        return mApplication;
    }


    public static Activity getActivity() {
        return mActivity;
    }

    public static ComponentActivity getComponentActivity() {
        return (ComponentActivity) mActivity;
    }

    /**
     * 添加Activity
     */
    public void addActivity(Activity activity) {
        // 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity(Activity activity) {
        //判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity() {
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
    }

    public void removeAllActivity(Activity currentActivity) {
        for (Activity activity : oList) {
            if (activity != currentActivity)
                activity.finish();
        }
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
        addActivity(activity);
    }

    /**
     * 初始化Toasty插件
     */
    private void initToasty() {

        Toasty.Config.getInstance()
                .tintIcon(true) // optional (apply textColor also to the icon)
                // .setToastTypeface() // optional
                //.setTextSize() // optional
                .allowQueue(true) // optional (prevents several Toastys from queuing)
                .setGravity(Gravity.CENTER) // optional (set toast gravity, offsets are optional)
                .supportDarkTheme(true) // optional (whether to support dark theme or not)
                //.setRTL(boolean isRTL) // optional (icon is on the right)
                .apply();
    }

    /**
     * 成功的提示
     */
    public static void showToastSuccess(String text) {
        Toasty.custom(getActivity(),
                text,
                R.mipmap.ic_success,
                R.color.colorBlack,
                R.color.colorWhite,
                true,
                true
        ).show();
    }


    /**
     * 失败的提示
     */
    public static void showToastFailed(String text) {
        Toasty.custom(getActivity(),
                text,
                R.mipmap.ic_warning,
                R.color.colorBlack,
                R.color.colorWhite,
                true,
                true
        ).show();
    }


    /**
     * 加载中的提示
     */
    public static void showToastLoading(String text) {
        Toasty.custom(getActivity(),
                text,
                R.mipmap.ic_waiting,
                R.color.colorBlack,
                R.color.colorWhite,
                true,
                true
        ).show();
    }


    /**
     * 成功提示框
     **/
    public static void showSuccessDialog(Activity activity, String text) {

        if (dialog != null) {
            Log.e(BleCallback.class.getSimpleName(), "showSuccessDialog关闭框1" + dialog);
            dialog.dismiss();
            dialog = null;
        }

        dialog = new Dialog(activity, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(false);
        ImageView imageView =  dialog.findViewById(R.id.ivToastIcon);
        TextView textView =  dialog.findViewById(R.id.tvToastText);

        textView.setText(text);

        dialog.show();

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 2000);

    }


    /**
     * 等待提示框
     **/
    public static void showLoadingDialog(Activity activity, String text, @Nullable int milliscond) {

        if (dialog != null) {
            Log.e(BleCallback.class.getSimpleName(), "showLoadingDialog关闭框1;" + text + ";" + dialog);
            dialog.dismiss();
            dialog = null;
        }

        dialog = new Dialog(MyApplication.getInstance().getCurrentActivity(), R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(false);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.ivToastIcon);
        TextView textView = (TextView) dialog.findViewById(R.id.tvToastText);

        imageView.setImageResource(R.mipmap.ic_waiting);
        textView.setText(text);

        dialog.show();

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, milliscond);
    }


    /**
     * 失败提示框
     **/
    public static void showFailedDialog(Activity activity, String text) {

        if (dialog != null) {
            Log.e(BleCallback.class.getSimpleName(), "showFailedDialog关闭框1" + dialog);
            dialog.dismiss();
            dialog = null;
        }

        dialog = new Dialog(activity, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(false);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.ivToastIcon);
        TextView textView = (TextView) dialog.findViewById(R.id.tvToastText);

        imageView.setImageResource(R.mipmap.ic_warning);
        textView.setText(text);
        dialog.show();

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 2000);
    }


    private static Handler handler = new Handler();
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    };


    public UserInfo getUserInfo() {
        return userInfo;
    }

    public boolean isSysAdmin() {
        return userInfo == null ? false : userInfo.isSysAdmin();
    }


    public Activity getCurrentActivity() {
        return oList.get(oList.size() - 1);
    }

}
