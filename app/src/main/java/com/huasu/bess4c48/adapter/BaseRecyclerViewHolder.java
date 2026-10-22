package com.huasu.bess4c48.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    public BaseRecyclerViewHolder(View convertView) {
        super(convertView);
    }

    /**
     * 点击事件回调
     */
    public interface OnItemClickListener {
        /**
         * on item click call back
         *
         * @param convertView convertView
         * @param position    position
         */
        void onItemClick(View convertView, int position);
    }

    /**
     * 长点击事件回调
     */
    public interface OnItemLongClickListener {
        /**
         * on item long click call back
         *
         * @param convertView convertView
         * @param position    position
         */
        boolean onItemLongClick(View convertView, int position);
    }
}
