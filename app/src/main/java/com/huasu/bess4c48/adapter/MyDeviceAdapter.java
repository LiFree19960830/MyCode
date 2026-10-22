package com.huasu.bess4c48.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.MyDevice;

import java.util.List;


public class MyDeviceAdapter extends BaseRecyclerViewAdapter {
    private final List<MyDevice> lists;

    public MyDeviceAdapter(List<MyDevice> lists) {
        this.lists = lists;
    }

    @Override
    protected BaseRecyclerViewHolder onCreatedRecycleViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_device, null));
    }

    @Override
    protected void onBindRecycleViewHolder(@NonNull BaseRecyclerViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        MyDevice item = getItem(position);
        holder.mTvAddress.setText(item.getDevice().getAddress());
        holder.mTvName.setText(TextUtils.isEmpty(item.getDevice().getName())?"Unknown":item.getDevice().getName());
        holder.mTvRssi.setText(item.getRssi() + "dBm");
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public MyDevice getItem(int position) {
        return lists.get(position);
    }

    public class ViewHolder extends BaseRecyclerViewHolder {

        TextView mTvName;
        TextView mTvAddress;
        TextView mTvRssi;
        public ViewHolder(View convertView) {
            super(convertView);
            mTvName = (TextView) convertView.findViewById(R.id.tvName);
            mTvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            mTvRssi = (TextView) convertView.findViewById(R.id.tvRssi);
        }
    }
}
