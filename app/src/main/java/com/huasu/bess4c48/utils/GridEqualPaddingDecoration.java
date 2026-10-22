package com.huasu.bess4c48.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class GridEqualPaddingDecoration extends RecyclerView.ItemDecoration {
    private final int mSpanCount; // 列数
    private final int mTotalHorizontalPadding; // 整体左右总间距（px）
    private final int mColumnSpacing; // 列间距（px）

    /**
     * 网格布局左右间距平分的ItemDecoration
     *
     * @param spanCount              列数
     * @param totalHorizontalPaddingDp 整体左右总间距（dp）
     * @param columnSpacingDp          列之间的间距（dp）
     */
    public GridEqualPaddingDecoration(int spanCount, int totalHorizontalPaddingDp, int columnSpacingDp, RecyclerView recyclerView) {
        mSpanCount = spanCount;
        Context context = recyclerView.getContext();
        mTotalHorizontalPadding = dp2px(totalHorizontalPaddingDp, context);
        mColumnSpacing = dp2px(columnSpacingDp, context);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (!(layoutManager instanceof GridLayoutManager)) {
            return;
        }

        GridLayoutManager gridManager = (GridLayoutManager) layoutManager;
        // 获取当前子项的列位置（从0开始）
        int position = parent.getChildAdapterPosition(view);
        int column = position % mSpanCount;

        // 核心：计算每列的左右偏移，保证整体左右对称
        // 总可用偏移量 = 整体左右间距 + (列数-1)*列间距
        int totalAvailablePadding = mTotalHorizontalPadding + (mSpanCount - 1) * mColumnSpacing;
        // 每列的基础偏移量 = 总可用偏移量 / 列数
        int perColumnPadding = totalAvailablePadding / mSpanCount;

        // 左偏移：列位置 * (perColumnPadding - 列间距) + 整体左间距/2
        outRect.left = column * (perColumnPadding - mColumnSpacing) + (mTotalHorizontalPadding / 2);
        // 右偏移：perColumnPadding - outRect.left - mColumnSpacing
        outRect.right = perColumnPadding - outRect.left - mColumnSpacing;

        // 可选：上下间距
        outRect.top = dp2px(8, parent.getContext());
        outRect.bottom = dp2px(8, parent.getContext());
    }

    // dp转px工具方法
    private int dp2px(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

}
