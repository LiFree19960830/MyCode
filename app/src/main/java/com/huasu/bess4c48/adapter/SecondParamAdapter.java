package com.huasu.bess4c48.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.DeviceParam;
import com.huasu.bess4c48.bean.ParentParam;
import com.huasu.bess4c48.view.SwitchLayout;
import com.huasu.bess4c48.view.Tab;

import java.util.List;


public class SecondParamAdapter extends BaseRecyclerViewAdapter {
    private List<DeviceParam> lists;
    private int typeSwitch = 10087;

    public SecondParamAdapter(List<DeviceParam> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    protected BaseRecyclerViewHolder onCreatedRecycleViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        if (viewType == typeSwitch) {
            return new ViewHolder(inflater.inflate(R.layout.layout_param_item_switch, parent, false));
        }
        return new ViewHolder(inflater.inflate(R.layout.layout_param_item, parent, false));
    }

    @Override
    protected void onBindRecycleViewHolder(@NonNull BaseRecyclerViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final DeviceParam item = getItem(position);
        holder.tv.setText(item.paramName);
        holder.tvVal.setText(item.getConvertVal());
        if (holder.iv != null) {
            holder.iv.setVisibility(item.imgResId != 0 ? View.VISIBLE : View.GONE);
            holder.iv.setImageResource(item.imgResId);
        }
        if (position == getItemCount() - 1)
            holder.view.setVisibility(View.GONE);
        holder.tvVal.setTextColor(item.isSetting() ? 0xff1179FD : 0x4b48575e);
        if (holder.ivSwitch != null) {
            holder.ivSwitch.setAlpha(position == 1 ? 1f : 0.7f);
            holder.ivSwitch.switchButton(item.checked ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        }

    }

    @Override
    public int getItemViewType(int position) {
        DeviceParam item = getItem(position);
        if (item.itemType == 2)
            return typeSwitch;
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public DeviceParam getItem(int position) {
        return lists.get(position);
    }


    public class ViewHolder extends BaseRecyclerViewHolder {

        TextView tv;
        TextView tvVal;
        @Nullable
        SwitchLayout ivSwitch;
        View view;
        @Nullable
        ImageView iv;

        public ViewHolder(View convertView) {
            super(convertView);
            tv = convertView.findViewById(R.id.tv);
            tvVal = convertView.findViewById(R.id.tvVal);
            ivSwitch = convertView.findViewById(R.id.ivSwitch);
            view = convertView.findViewById(R.id.view);
            iv = convertView.findViewById(R.id.iv);
        }
    }
}
