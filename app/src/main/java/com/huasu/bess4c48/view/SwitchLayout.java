package com.huasu.bess4c48.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.utils.ToolUtil;

public class SwitchLayout extends LinearLayout {

    TextView mTvSwitch;
    RelativeLayout mRlSwitch;

    private int mLayoutWidth = ToolUtil.dp2px(32);
    private int mLayoutHeight = ToolUtil.dp2px(16);
    private int mTextWidth = ToolUtil.dp2px(15);
    private int mTextHeight = ToolUtil.dp2px(15);

    private Context mContext;
    private AttributeSet mAttrs;

    public SwitchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAttrs = attrs;
        LayoutInflater.from(context).inflate(R.layout.layout_switch, this);
        mTvSwitch = findViewById(R.id.tvSwitch);
        mRlSwitch = findViewById(R.id.rlSwitch);
        initView();

    }

    private void initView() {
        TypedArray typedArray = mContext.obtainStyledAttributes(mAttrs, R.styleable.switch_layout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ToolUtil.dp2px(32), ToolUtil.dp2px(16));
        layoutParams.width = typedArray.getInteger(R.styleable.switch_layout_layoutWidth, mLayoutWidth);
        layoutParams.height = typedArray.getInteger(R.styleable.switch_layout_layoutHeight, mLayoutHeight);
        mRlSwitch.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ToolUtil.dp2px(15), ToolUtil.dp2px(15));
        textParams.width = typedArray.getInteger(R.styleable.switch_layout_textWidth, mTextWidth);
        textParams.height = typedArray.getInteger(R.styleable.switch_layout_textHeight, mTextHeight);
        mTvSwitch.setLayoutParams(textParams);
        typedArray.recycle();
    }

    /**
     * 显示switch上的文字，以及switch切换到的方向
     **/
    public void switchButton(int strResId, int switchDirection) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTvSwitch.getLayoutParams();
        switch (switchDirection) {
            case RelativeLayout.ALIGN_PARENT_RIGHT:
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                setSwitchBgRes(R.drawable.shape_round_blue);
                break;
            case RelativeLayout.ALIGN_PARENT_LEFT:
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                setSwitchBgRes(R.drawable.shape_round_white);
                break;
        }
        mTvSwitch.setText(strResId);
        mTvSwitch.setLayoutParams(layoutParams);

    }
    /**
     * 显示switch上的文字，以及switch切换到的方向
     **/
    public void switchButton(int switchDirection) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTvSwitch.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        switch (switchDirection) {
            case RelativeLayout.ALIGN_PARENT_RIGHT:
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                setSwitchBgRes(R.drawable.shape_round_blue);
                break;
            case RelativeLayout.ALIGN_PARENT_LEFT:
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                setSwitchBgRes(R.drawable.shape_round_white);
                break;
        }
        mTvSwitch.setLayoutParams(layoutParams);

    }

    public void setSwitchBg() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTvSwitch.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        mTvSwitch.setLayoutParams(layoutParams);
    }


    public void setSwitchBgRes(int bgRes) {
        mTvSwitch.setBackgroundResource(bgRes);
    }

    public void setLayoutWidth(int layoutWidth) {
        this.mLayoutWidth = layoutWidth;
        initView();
        requestLayout();
        invalidate();
    }

    public void setLayoutHeight(int layoutHeight) {
        this.mLayoutHeight = layoutHeight;
        initView();
        invalidate();
    }

    public void setTextWidth(int textWidth) {
        this.mTextWidth = textWidth;
        initView();
        invalidate();
    }

    public void setTextHeight(int textHeight) {
        this.mTextHeight = textHeight;
        initView();
        invalidate();
    }
}
