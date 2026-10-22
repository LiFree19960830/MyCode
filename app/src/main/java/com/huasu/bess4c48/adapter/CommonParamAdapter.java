package com.huasu.bess4c48.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.DeviceParam;
import com.huasu.bess4c48.bean.ParentParam;
import com.huasu.bess4c48.utils.ToolUtil;
import com.huasu.bess4c48.view.Tab;

import java.util.List;


public class CommonParamAdapter extends BaseRecyclerViewAdapter {
    private List<ParentParam> lists;
    public OnClick mOnClick;

    public CommonParamAdapter(List<ParentParam> lists, OnClick onClick) {
        this.lists = lists;
        this.mOnClick = onClick;
        notifyDataSetChanged();
    }

    public interface OnClick {
        void onClick(int position);

        void onChildClick(int parentPosition, int childPosition, DeviceParam child, String parentName);
    }

    @Override
    protected BaseRecyclerViewHolder onCreatedRecycleViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(inflater.inflate(R.layout.layout_common_param_item, parent, false));
    }

    @Override
    protected void onBindRecycleViewHolder(@NonNull BaseRecyclerViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final ParentParam item = getItem(position);
        holder.tab.setTitle(item.title);
        holder.tab.setBtnVisible(item.isShowReadBtn ? View.VISIBLE : View.GONE);
        if (item.isShowControl) {
            holder.tab.setControlVisible(View.VISIBLE, item.isStorageControl);
        } else {
            holder.tab.setControlVisible(View.GONE, item.isStorageControl);
        }
        holder.tab.setVisibility(item.isShowTab ? View.VISIBLE : View.GONE);
        SecondParamAdapter secondParamAdapter = new SecondParamAdapter(item.childs);
        holder.rvChild.setLayoutManager(new LinearLayoutManager(mContext));
        holder.rvChild.setAdapter(secondParamAdapter);
        holder.tab.getBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClick != null)
                    mOnClick.onClick(position);

            }
        });
        holder.tab.getSwitchLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClick != null)
                    mOnClick.onClick(position);

            }
        });
        secondParamAdapter.setOnItemClickListener(new BaseRecyclerViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, int childPosition) {
                DeviceParam child = item.childs.get(childPosition);
                if (mOnClick != null && child.isSetting()) {
                    mOnClick.onChildClick(position, childPosition, child, item.title);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    public ParentParam getItem(int position) {
        return lists.get(position);
    }


    public DeviceParam getChildItem(int parentPosition, int childPosition) {
        ParentParam parentParam = getItem(parentPosition);
        return parentParam.childs.get(childPosition);
    }


    public class ViewHolder extends BaseRecyclerViewHolder {

        Tab tab;
        RecyclerView rvChild;

        public ViewHolder(View convertView) {
            super(convertView);
            tab = convertView.findViewById(R.id.tab);
            rvChild = convertView.findViewById(R.id.rvChild);
        }
    }

}
