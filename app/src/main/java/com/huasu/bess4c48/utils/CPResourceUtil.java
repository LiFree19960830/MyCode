package com.huasu.bess4c48.utils;

import android.content.Context;
import android.util.Log;

import com.huasu.bess4c48.MyApplication;
public class CPResourceUtil {

    private static final String TAG = "CPResourceUtil";
    public static int getLayoutId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "layout",
                paramContext.getPackageName());
    }

    public static int getStringId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "string",
                paramContext.getPackageName());
    }

    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "drawable", paramContext.getPackageName());
    }

    public static int getStyleId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "style", paramContext.getPackageName());
    }

    public static int getId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "id", paramContext.getPackageName());
    }

    public static int getColorId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "color", paramContext.getPackageName());
    }

    public static int getArrayId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "array", paramContext.getPackageName());
    }

    public static String getResStringById(String name) {
        try {
            int viewId = getStringId(MyApplication.mApplication, name);
            return MyApplication.mApplication.getResources().getString(viewId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public static String getResStringByResId(int resStrId) {
        try {
            return MyApplication.mApplication.getResources().getString(resStrId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     *  根据code获取告警信息
     */
    public static String getAlarmInfoByCode(int code) {
        return CPResourceUtil.getResStringById("alarm_code_" + code);
    }

    /**
     *  根据code获取保护信息
     */
    public static String getProtectInfoByCode(int code) {
        return CPResourceUtil.getResStringById("protect_code_" + code);
    }


    /**
     *  根据code获取保护信息
     */
    public static String getChargeStateInfoByCode(int code) {
        return CPResourceUtil.getResStringById("charge_state_code_" + code);
    }


}
