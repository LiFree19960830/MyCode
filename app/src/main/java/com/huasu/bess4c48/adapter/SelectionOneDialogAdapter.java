package com.huasu.bess4c48.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.MyDevice;
import com.huasu.bess4c48.bean.SelectionData;
import com.huasu.bess4c48.utils.CPResourceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉单选框的Adapter
 */
public class SelectionOneDialogAdapter extends RecyclerView.Adapter<SelectionOneDialogAdapter.ViewHolder> {


    private List<SelectionData> mSelectionDataList = new ArrayList<>();

    private int checkIndex = 0;
    private boolean isShowCheckDefault = false;

    private MyOnClickListener mlistener;


    public SelectionOneDialogAdapter() {
    }

    public void setData(List<SelectionData> list) {
        mSelectionDataList.clear();
        mSelectionDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(MyOnClickListener listener) {
        this.mlistener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View selectionView;
        TextView mTvName;

        public ViewHolder(View view) {
            super(view);
            selectionView = view;
            mTvName = (TextView) view.findViewById(R.id.tvName);
        }

    }


    @NonNull
    @Override
    public SelectionOneDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_selection_one_item, parent, false);
        final SelectionOneDialogAdapter.ViewHolder viewHolder = new SelectionOneDialogAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        SelectionData item = mSelectionDataList.get(position);
        if (position == checkIndex) {
            holder.mTvName.setSelected(true);
        } else {
            holder.mTvName.setSelected(false);
        }

        holder.mTvName.setText(item.text);

        if (mlistener != null) {
            holder.selectionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mlistener.onClick (position);
                }
            });
        }
    }


    public void setCheckIndex(int newCheckedIndex) {
        int oldPos = -1;
        int newPos = -1;
        notifyItemChanged(this.checkIndex);
        this.checkIndex = newCheckedIndex;
        notifyItemChanged(checkIndex);
        notifyItemChanged(oldPos);
        notifyItemChanged(newPos);
    }

    @Override
    public int getItemCount() {
        return mSelectionDataList.size();
    }


    public void setShowCheckDefault(boolean showCheckDefault) {
        isShowCheckDefault = showCheckDefault;
    }

    public interface MyOnClickListener {
        void onClick(int position);
    }
}
