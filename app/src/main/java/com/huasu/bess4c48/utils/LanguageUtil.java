package com.huasu.bess4c48.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * 语言切换工具类（不跟随系统）
 * 支持语言：中文(zh)、英文(en)、其他可扩展
 */
public class LanguageUtil {
    // 保存语言的SP Key
    private static final String SP_KEY_LANGUAGE = "app_selected_language";
    // 默认语言（中文）
    private static final String DEFAULT_LANGUAGE = "zh-rCN";
    // 语言标识常量
    public static final String LOCALE_ZH = "zh-rCN"; // 简体中文
    public static final String LOCALE_EN = "en"; // 英文
    public static final String LOCALE_ZH_TW = "zh-TW"; // 繁体中文

    /**
     * 保存选择的语言到SP
     *
     * @param context  上下文
     * @param language 语言标识（zh=中文，en=英文，ja=日语等）
     */
    public static void saveLanguage(Context context, String language) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(SP_KEY_LANGUAGE, language).apply();
    }

    /**
     * 获取保存的语言（无则返回默认中文）
     */
    public static String getSavedLanguage(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//        return sp.getString(SP_KEY_LANGUAGE, DEFAULT_LANGUAGE);

        return sp.getString(SP_KEY_LANGUAGE, "");
    }

    /**
     * 初始化应用语言（在Application/BaseActivity中调用）
     */
    public static void initAppLanguage(Context context) {
        if (true)
            return;
        String language = getSavedLanguage(context);
        if (TextUtils.isEmpty(language))
            return;
        setAppLanguage(context, language);
    }

    /**
     * ✅ 核心方法：设置应用语言（主方法，调用这个即可）
     *
     * @param context    上下文
     * @param localeType 语言标识 LOCALE_ZH / LOCALE_EN / LOCALE_ZH_TW
     */
    public static void setAppLanguage(Context context, String localeType) {
        // 1. 先保存语言偏好
        saveLanguage(context, localeType);
        Locale targetLocale = getLocaleByType(localeType);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        //=========== Android 12(API31) 及以上 核心修复 - 系统原生API，百分百生效 ===========
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LocaleList localeList = new LocaleList(targetLocale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.createConfigurationContext(config);
        }
        //=========== Android 7.0 ~ 11 兼容 ===========
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(targetLocale);
            context.createConfigurationContext(config);
        }
        //=========== Android 7.0以下 兼容 ===========
        else {
            config.locale = targetLocale;
        }
        resources.updateConfiguration(config, dm);
    }

    /**
     * 根据语言标识，获取对应的Locale对象
     */
    public static Locale getLocaleByType(String localeType) {
        switch (localeType) {
            case LOCALE_EN:
                return Locale.ENGLISH;
            case LOCALE_ZH_TW:
                return Locale.TRADITIONAL_CHINESE;
            case LOCALE_ZH:
            default:
                return Locale.SIMPLIFIED_CHINESE;
        }
    }

    /**
     * 重启当前页面，让语言设置立即生效
     *
     * @param context 当前Activity
     * @param cls     要跳转的主页面（如MainActivity.class）
     */
    public static void restartActivity(Context context, Class<?> cls) {
        android.content.Intent intent = new android.content.Intent(context, cls);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((android.app.Activity) context).finish();
        System.exit(1);
    }

}
