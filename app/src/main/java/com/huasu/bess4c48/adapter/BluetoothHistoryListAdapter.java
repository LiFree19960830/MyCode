package com.huasu.bess4c48.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.MyBleDevice;
import com.huasu.bess4c48.bean.MyDevice;

import java.util.ArrayList;
import java.util.List;

public class BluetoothHistoryListAdapter extends RecyclerView.Adapter<BluetoothHistoryListAdapter.ViewHolder> {

    private List<MyBleDevice> myDeviceList = new ArrayList<>();

    private BluetoothHistoryListAdapter.onClickListener myListener;


    static class ViewHolder extends RecyclerView.ViewHolder {
        View wrapper;

        EditText edDeviceName;
        TextView tvEditBtn;

        TextView tvSaveBtn;

        TextView tvDeleteBtn;
        TextView tvConnectBtn;

        public ViewHolder(View view) {
            super(view);
            wrapper = view;
            edDeviceName = (EditText) view.findViewById(R.id.edDeviceName);
            tvEditBtn = (TextView) view.findViewById(R.id.tvEditBtn);
            tvSaveBtn = (TextView) view.findViewById(R.id.tvSaveBtn);
            tvDeleteBtn = (TextView) view.findViewById(R.id.tvDeleteBtn);
            tvConnectBtn = (TextView) view.findViewById(R.id.tvConnectBtn);
        }

    }

    public BluetoothHistoryListAdapter() {

    }


    public void setData(List<MyBleDevice> list) {
        myDeviceList.clear();
        myDeviceList.addAll(list);
        notifyDataSetChanged();
    }


    public void setListener(BluetoothHistoryListAdapter.onClickListener listener) {
        this.myListener = listener;
    }

    @Override
    public int getItemCount() {
        return this.myDeviceList.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_bluetooth_history_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final MyBleDevice device = myDeviceList.get(position);
        String deviceName = device.getBleName();
        holder.edDeviceName.setText(deviceName);
        holder.edDeviceName.setTag(device);
        holder.tvEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager manager = (InputMethodManager) MyApplication
                        .mApplication.getSystemService(Context.INPUT_METHOD_SERVICE);
                holder.edDeviceName.setEnabled(true);
                holder.tvEditBtn.setVisibility(View.GONE);
                holder.tvSaveBtn.setVisibility(View.VISIBLE);
                holder.edDeviceName.requestFocus();
                holder.edDeviceName.setSelection(holder.edDeviceName.getText().length());
                manager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                holder.edDeviceName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            manager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                        } else {
                            MyBleDevice device = (MyBleDevice) holder.edDeviceName.getTag();
                            String deviceName = device.getBleName();
                            holder.edDeviceName.setText(deviceName);
                            holder.edDeviceName.setEnabled(false);
                            holder.tvEditBtn.setVisibility(View.VISIBLE);
                            holder.tvSaveBtn.setVisibility(View.GONE);
                        }
                    }
                });
                if (myListener != null) {
                    myListener.onEditBtnClick(device);
                }
            }
        });
        holder.tvSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myListener != null) {
                    device.updateName = holder.edDeviceName.getText().toString();
                    myListener.onSaveBtnClick(device);
                }
                holder.edDeviceName.setEnabled(false);
                holder.tvEditBtn.setVisibility(View.VISIBLE);
                holder.tvSaveBtn.setVisibility(View.GONE);
            }
        });
        holder.tvConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myListener != null) {
                    myListener.onConnectBtnClick(device);
                }
            }
        });

        holder.tvDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myListener != null) {
                    myListener.onDeleteBtnClick(device);
                    holder.tvDeleteBtn.setClickable(false);
                }
            }
        });
    }


    public abstract interface onClickListener {
        void onConnectBtnClick(MyBleDevice device);

        void onEditBtnClick(MyBleDevice device);

        void onSaveBtnClick(MyBleDevice device);

        void onDeleteBtnClick(MyBleDevice device);
    }

    ;
}
