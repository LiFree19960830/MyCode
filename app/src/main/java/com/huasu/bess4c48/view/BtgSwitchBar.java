package com.huasu.bess4c48.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.utils.CPResourceUtil;

public class BtgSwitchBar extends LinearLayout {

    private ImageView imageView;
    private TextView tvText;
    private TextView tvStatus;

    public BtgSwitchBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_btg_switch_bar,this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.BtgSwitchBar);
        String text = typedArray.getString(R.styleable.BtgSwitchBar_switch_text);
        Integer icon = typedArray.getResourceId(R.styleable.BtgSwitchBar_switch_icon, R.mipmap.ic_mos);

        imageView = (ImageView) findViewById(R.id.ivSwitch);
        tvText = (TextView) findViewById(R.id.tvText);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        imageView.setBackgroundResource(icon);
        tvText.setText(text);

        typedArray.recycle();
    }


    public void setStatus(String status) {
        tvStatus.setText(status);
    }

    public void setStatus(int status) {
        tvStatus.setText(
          status==1?CPResourceUtil.getResStringByResId(R.string.on):CPResourceUtil.getResStringByResId(R.string.off)
        );
    }
}