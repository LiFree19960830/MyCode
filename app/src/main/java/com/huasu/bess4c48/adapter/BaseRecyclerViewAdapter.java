package com.huasu.bess4c48.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.huasu.bess4c48.R;

import java.util.List;


/**
 * Created by lt on 2016/5/25.
 */
public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> implements View
        .OnClickListener, View.OnLongClickListener {

    protected BaseRecyclerViewHolder.OnItemClickListener mOnItemClickListener;
    protected BaseRecyclerViewHolder.OnItemLongClickListener mOnItemLongClickListener;
    protected Context mContext;

    protected int mItemHeight = 0;
    /**
     * 对接了onCreateViewHolder,原先onCreateViewHolder里的逻辑写在这
     *
     * @return
     */
    protected abstract BaseRecyclerViewHolder onCreatedRecycleViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent,
                                                                         int viewType);

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        mContext = parent.getContext();
        mItemHeight = mContext.getResources().getDimensionPixelSize(R.dimen.table_item_height);
        return onCreatedRecycleViewHolder(LayoutInflater.from(mContext), parent, position);
    }

    /**
     * 对接了onBindViewHolder,原先onBindViewHolder里的逻辑写在这
     *
     * @param viewHolder viewHolder
     * @param position   position
     */
    protected abstract void onBindRecycleViewHolder(@NonNull BaseRecyclerViewHolder viewHolder, int position);

    /**
     * 1，如果仅仅想刷新view里的某几个控件，则重写此方法
     * 2，并且调用notifyItemChanged(int position, Object payload)，最后一个参数不能为null，（notifyItemChanged(int positon)是刷新整个item视图）
     * @param viewHolder
     * @param position
     * @param payloads
     */
    protected void onBindRefreshViewHolder(BaseRecyclerViewHolder viewHolder, int position, List<Object> payloads) {}

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            onBindRefreshViewHolder(holder, position, payloads);
        }

        holder.itemView.setTag(position);
        if (mOnItemClickListener != null)
            holder.itemView.setOnClickListener(this);

        if (mOnItemLongClickListener != null)
            holder.itemView.setOnLongClickListener(this);


    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        onBindRecycleViewHolder(holder, position);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (null != mOnItemClickListener)
            mOnItemClickListener.onItemClick(v, position);
    }

    @Override
    public boolean onLongClick(View v) {
        int position = (int) v.getTag();
        if (null != mOnItemLongClickListener)
            return mOnItemLongClickListener.onItemLongClick(v, position);
        return false;
    }

    /**
     * 设置点击事件
     *
     * @param onItemClickListener onItemClickListener
     */
    public void setOnItemClickListener(BaseRecyclerViewHolder.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    /**
     * 设置长点击事件
     *
     * @param onItemLongClickListener onItemLongClickListener
     */
    public void setOnItemLongClickListener(BaseRecyclerViewHolder.OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }
}
