package com.huasu.bess4c48.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class ScreenFullUtils {
    // Android 12+ 虚拟导航栏适配核心方法
    public static void fixNavigationBarOverlay(final Activity activity) {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();

        // 1. 核心设置：布局延伸到导航栏，但通过内边距避开遮挡
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+（含12）官方API，替代setDecorFitsSystemWindows
            window.setDecorFitsSystemWindows(false);

            // 2. 获取WindowInsetsController，控制导航栏行为
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                // 导航栏透明（可选，根据需求设置）
                window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);
                // 显示导航栏（如果需要隐藏，改为hide(WindowInsets.Type.navigationBars())）
                insetsController.show(WindowInsets.Type.navigationBars());
                // 关键：设置导航栏行为 - 滑动边缘呼出，松手隐藏（避免遮挡）
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }

        // 3. 监听WindowInsets变化，动态设置根布局内边距（核心：避开导航栏高度）
        decorView.setOnApplyWindowInsetsListener((new View.OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
                int bottomInset = 0;
                if (android.os.Build.VERSION.SDK_INT >= 30) {
                    bottomInset = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
                    // 获取根布局（比如Activity的contentView）
                    View rootView = activity.findViewById(android.R.id.content);
                    // 设置根布局底部内边距 = 导航栏高度，避免内容被遮挡
                    rootView.setPadding(rootView.getPaddingLeft(),
                            rootView.getPaddingTop(),
                            rootView.getPaddingRight(),
                            bottomInset);
                    // 消费Insets，避免重复计算
                    return insets.consumeSystemWindowInsets();
                }
               return null;
            }
        }));
    }
}
