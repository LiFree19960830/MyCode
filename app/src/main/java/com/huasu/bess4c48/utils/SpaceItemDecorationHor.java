package com.huasu.bess4c48.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by Administrator on 2017/3/21.
 * 针对两列左右平分间距
 */

public class SpaceItemDecorationHor extends RecyclerView.ItemDecoration {

    private int space;
    private int bottom = -1;

    public SpaceItemDecorationHor(int space, int bottom) {
        this.space = space;
        this.bottom = bottom;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int spanCount = 0;
        if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            spanCount = manager.getSpanCount();
        } else if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
            spanCount = manager.getSpanCount();
        } else {
            spanCount = 1;
        }
        int pos = parent.getChildAdapterPosition(view);
        // 计算这个child 处于第几列
        int column = (pos) % spanCount;

        outRect.left = (column * space / spanCount);
        outRect.right = space - (column + 1) * space / spanCount;

        int row = pos / spanCount;//第几行
        if (row != 0)//如果是第一行，那么不设置上边距
            outRect.top = bottom;
        outRect.right = space;
        outRect.bottom = bottom;
    }
}
