package com.huasu.bess4c48.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.utils.CPResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class ProtectInfoAdapter extends RecyclerView.Adapter<ProtectInfoAdapter.ViewHolder> {

    private List<String> mProtectInfoList = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder{
        View AlarmInfoView;
        TextView tvTableNo;
        TextView tvAlarmInfo;
        public ViewHolder(View view){
            super(view);
            AlarmInfoView = view;
            tvTableNo = (TextView) view.findViewById(R.id.tvTableNo);
            tvAlarmInfo = (TextView) view.findViewById(R.id.tvAlarmInfo);
        }

    }

    public ProtectInfoAdapter(){

    }


    public void setData(List<String> list) {
        mProtectInfoList.clear();
        if(list.size()>0){
            mProtectInfoList.add(CPResourceUtil.getResStringByResId(R.string.protect_info));
        }
        mProtectInfoList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mProtectInfoList.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_alarm_info_list, parent, false);
        final  ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        if(position==0){
            holder.tvTableNo.setText(CPResourceUtil.getResStringByResId(R.string.table_no));
            holder.tvAlarmInfo.setText(CPResourceUtil.getResStringByResId(R.string.protect_info));
        }else{
            String info = mProtectInfoList.get(position);
            holder.tvTableNo.setText(String.valueOf(position));
            holder.tvAlarmInfo.setText(info);
        }
    }
}
