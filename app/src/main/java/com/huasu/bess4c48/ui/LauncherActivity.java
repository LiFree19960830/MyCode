package com.huasu.bess4c48.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseActivity;
import com.huasu.bess4c48.ui.login.LoginActivity;

import es.dmoral.toasty.Toasty;

public class LauncherActivity extends BaseActivity {

    private static final String TAG = "launcherActivity";
    private Handler mhandler = new Handler();

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initGif();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LoginActivity.launchActivity();
                finish();
            }
        };
        mhandler.postDelayed(runnable,2000);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_launcher;
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
     *  初始化GIF
     */
    private void initGif(){
        imageView = findViewById(R.id.ivLauncherGif);
        Glide.with(this).load(R.drawable.launcher2).into(imageView);
    }


    private void showToastSuccess(String text){
        Toasty.custom(this,
                text,
                R.mipmap.ic_success,
                R.color.colorBlack,
                R.color.colorWhite,
                true,
                true
        ).show();
    }

}