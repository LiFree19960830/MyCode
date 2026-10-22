package com.huasu.bess4c48.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.SettingItem;

import java.util.List;

public class SettingAdapter extends BaseRecyclerViewAdapter {
    private List<SettingItem> lists;

    public SettingAdapter(List<SettingItem> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    protected BaseRecyclerViewHolder onCreatedRecycleViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(inflater.inflate(R.layout.layout_setting_item, parent, false));
    }

    @Override
    protected void onBindRecycleViewHolder(@NonNull BaseRecyclerViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final SettingItem item = getItem(position);
        holder.iv.setImageResource(item.imgRes);
        holder.tv.setText(item.titleRes);
    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    public SettingItem getItem(int position) {
        return lists.get(position);
    }

    public class ViewHolder extends BaseRecyclerViewHolder {

        ImageView iv;
        TextView tv;

        public ViewHolder(View convertView) {
            super(convertView);
            iv = convertView.findViewById(R.id.iv);
            tv = convertView.findViewById(R.id.tv);
        }
    }

}
