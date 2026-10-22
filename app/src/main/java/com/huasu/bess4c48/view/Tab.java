package com.huasu.bess4c48.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.utils.CPResourceUtil;

public class Tab extends LinearLayout {

    private TextView tvText, tvCount;
    private Button btn;
    private LinearLayout llControl;
    private SwitchLayout switchLayout;

    public Tab(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_tab, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Tab);
        String text = typedArray.getString(R.styleable.Tab_tab_text);
        tvText = findViewById(R.id.tv);
        tvText.setText(text);
        tvCount = findViewById(R.id.tvCount);
        btn = findViewById(R.id.btn);
        llControl = findViewById(R.id.llControl);
        switchLayout = findViewById(R.id.ivSwitch);
        typedArray.recycle();
    }

    public Button getBtn() {
        return btn;
    }

    public void setBtnVisible(int visibility) {
        btn.setVisibility(visibility);
    }

    public SwitchLayout getSwitchLayout() {
        return switchLayout;
    }

    public void setControlVisible(int visibility, boolean isStorageControl) {
        llControl.setVisibility(visibility);
        switchLayout.switchButton(isStorageControl ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
    }


    public void setCount(int bgRes, int count) {
        tvCount.setVisibility(VISIBLE);
        tvCount.setBackgroundResource(bgRes);
        tvCount.setText(String.valueOf(count));
    }

    public void setTitle(String title) {
        tvText.setText(title);
    }
}