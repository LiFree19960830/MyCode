package com.huasu.bess4c48.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.utils.CPResourceUtil;


/**
 *  暂无数据页面
 */
public class TableNoData extends LinearLayout {

    private LinearLayout llTableNoData;
    private ImageView imageView;
    private TextView textView;

    public TableNoData(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_table_no_data,this);

        llTableNoData =  (LinearLayout) findViewById(R.id.llTableNoData);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textview);
    }




    public void setVisible(int visible) {
        llTableNoData.setVisibility(visible);
    }


}