package com.huasu.bess4c48.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.SelectionData;
import com.huasu.bess4c48.utils.CPResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class AlarmInfoAdapter extends RecyclerView.Adapter<AlarmInfoAdapter.ViewHolder> {

    private List<String> mAlarmInfoList = new ArrayList<>();

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

    public AlarmInfoAdapter(){

    }


    public void setData(List<String> list) {
        mAlarmInfoList.clear();
        if(list.size()>0){
            mAlarmInfoList.add(CPResourceUtil.getResStringByResId(R.string.alarm_info));
        }
        mAlarmInfoList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mAlarmInfoList.size();
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
            holder.tvAlarmInfo.setText(CPResourceUtil.getResStringByResId(R.string.alarm_info));
        }else{
            String info = mAlarmInfoList.get(position);
            holder.tvTableNo.setText(String.valueOf(position));
            holder.tvAlarmInfo.setText(info);
        }
    }
}
