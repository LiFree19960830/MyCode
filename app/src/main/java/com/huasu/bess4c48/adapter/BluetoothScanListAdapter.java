package com.huasu.bess4c48.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.MyDevice;
import com.huasu.bess4c48.utils.CPResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class BluetoothScanListAdapter extends RecyclerView.Adapter<BluetoothScanListAdapter.ViewHolder> {

    private List<MyDevice> myDeviceList = new ArrayList<>();

    private BluetoothScanListAdapter.onClickListener myListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View wrapper;
        TextView tvDeviceName;
        TextView tvConnectBtn;
        public ViewHolder(View view){
            super(view);
            wrapper = view;
            tvDeviceName = (TextView) view.findViewById(R.id.tvDeviceName);
            tvConnectBtn = (TextView) view.findViewById(R.id.tvConnectBtn);
        }

    }

    public BluetoothScanListAdapter(){

    }


    public void setData(List<MyDevice> list) {
        myDeviceList.clear();
        myDeviceList.addAll(list);
        notifyDataSetChanged();
    }


    public List<MyDevice> getData() {
        List<MyDevice> list = new ArrayList<>();
        list.addAll(myDeviceList);
        return list;
    }

    public void setListener(BluetoothScanListAdapter.onClickListener listener) {
        this.myListener = listener;
    }

    @Override
    public int getItemCount() {
        return this.myDeviceList.size();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_bluetooth_scan_list, parent, false);
        final  ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final MyDevice device = myDeviceList.get(position);
        String name = TextUtils.isEmpty(device.getUpdateName())?device.getDevice().getName():device.getUpdateName();
        holder.tvDeviceName.setText(name);
        holder.tvConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myListener !=null){
                    myListener.onConnectBtnClick(device);
                }
            }
        });
    }


    public interface onClickListener{
        void onConnectBtnClick(MyDevice device);

    };
}
