package com.huasu.bess4c48.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.BTRealtimeData;
import com.huasu.bess4c48.utils.CPResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class CellVoltageAdapter extends RecyclerView.Adapter<CellVoltageAdapter.ViewHolder> {

    private List<BTRealtimeData> mDataList = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvCellName;
        TextView tvVoltage;
        ImageView iv1;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvCellName = view.findViewById(R.id.tvCellName);
            tvVoltage = view.findViewById(R.id.tvCellVoltage);
            iv1 = view.findViewById(R.id.iv1);
        }

    }

    public CellVoltageAdapter() {

    }


    public void setData(List<BTRealtimeData> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mDataList.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_cell_voltage_list, parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        BTRealtimeData btRealtimeData = mDataList.get(position);
        viewHolder.tvCellName.setText("cell" + (position + 1) + ":");
        viewHolder.tvVoltage.setText(String.valueOf(btRealtimeData.monomerVoltage));
        if (position % 4 == 0 || position % 4 == 1)
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        else
            viewHolder.itemView.setBackgroundColor(0XF8FBFEFF);
        if (btRealtimeData.isEquilibriuming()) {
            viewHolder.iv1.setVisibility(View.VISIBLE);
            viewHolder.tvCellName.setSelected(true);
            viewHolder.tvVoltage.setSelected(true);
        } else {
            viewHolder.iv1.setVisibility(View.INVISIBLE);
            viewHolder.tvCellName.setSelected(false);
            viewHolder.tvVoltage.setSelected(false);
        }

    }
}
