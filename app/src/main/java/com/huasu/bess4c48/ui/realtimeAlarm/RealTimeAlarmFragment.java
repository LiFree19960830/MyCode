package com.huasu.bess4c48.ui.realtimeAlarm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.adapter.AlarmInfoAdapter;
import com.huasu.bess4c48.adapter.ProtectInfoAdapter;
import com.huasu.bess4c48.bean.BTGRealtimeData;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.ui.MainActivity;
import com.huasu.bess4c48.utils.CPResourceUtil;
import com.huasu.bess4c48.utils.ConstantKit;
import com.huasu.bess4c48.view.Tab;
import com.huasu.bess4c48.view.TableNoData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 实时告警
 */
public class RealTimeAlarmFragment extends Fragment {

    private static final String TAG = "RealTimeAlarmFragment";
    private RecyclerView recyclerViewAlaramInfo;
    private RecyclerView recyclerViewProtectInfo;
    private ImageView ivAlarmInfoNoData;
    private TableNoData tableNoDataAlarmInfo;
    private Tab tabProtect;
    private Tab tabAlm;

    private TableNoData tableNoDataProtectInfo;

    private AlarmInfoAdapter alarmInfoAdapter;
    private ProtectInfoAdapter protectInfoAdapter;

    private List<String> alarmInfoList = new ArrayList<>();
    private List<String> protectInfoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realtime_alarm, container, false);
        recyclerViewAlaramInfo = view.findViewById(R.id.recyclerViewAlarmInfo);
        recyclerViewProtectInfo = view.findViewById(R.id.recyclerViewProtectInfo);
        tableNoDataAlarmInfo = view.findViewById(R.id.tableNoDataAlarmInfo);
        tableNoDataProtectInfo = view.findViewById(R.id.tableNoDataProtect);
        tabProtect = view.findViewById(R.id.tabProtect);
        tabAlm = view.findViewById(R.id.tabAlm);
        initRecyclerView();

        EventBus.getDefault().register(this);
        return view;
    }

    /**
     * 初始化RecyclerView
     */
    public void initRecyclerView() {
        LinearLayoutManager layoutManagerAlarmInfo = new LinearLayoutManager(this.getContext());
        recyclerViewAlaramInfo.setLayoutManager(layoutManagerAlarmInfo);
        alarmInfoAdapter = new AlarmInfoAdapter();
        recyclerViewAlaramInfo.setAdapter(alarmInfoAdapter);

        LinearLayoutManager layoutManagerProtectInfo = new LinearLayoutManager(this.getContext());
        recyclerViewProtectInfo.setLayoutManager(layoutManagerProtectInfo);
        protectInfoAdapter = new ProtectInfoAdapter();
        recyclerViewProtectInfo.setAdapter(protectInfoAdapter);
    }

    /**
     * 处理实时告警数据
     */
    public void handleRealTimeData(BTGRealtimeData btgRealtimeData) {
        alarmInfoList.clear();
        protectInfoList.clear();
        // List<Integer> alarmInfoList = Arrays.asList(1, 2,3,4,5,6,7,8);
        if (alarmInfoList.size() == 0) {
            tableNoDataAlarmInfo.setVisible(View.VISIBLE);
            recyclerViewAlaramInfo.setVisibility(View.GONE);
        } else {
            alarmInfoAdapter.setData(alarmInfoList);
            tableNoDataAlarmInfo.setVisible(View.GONE);
            recyclerViewAlaramInfo.setVisibility(View.VISIBLE);
        }

        List<Integer> alarms = btgRealtimeData.alarms;
        List<Integer> protects = btgRealtimeData.protects;
        tabAlm.setCount(R.drawable.circle_corner_f8cc14, 0);
        if (alarms != null && !alarms.isEmpty()) {
            tabAlm.setCount(R.drawable.circle_corner_f8cc14, alarms.size());
            for (Integer item : alarms) {
                List<Integer> cellNumbers = btgRealtimeData.alarmsCellNoMap.get(item);
                if (cellNumbers != null && !cellNumbers.isEmpty()) {
                    for (Integer cellNo : cellNumbers) {
                        alarmInfoList.add(cellNo + "#" + CPResourceUtil.getAlarmInfoByCode(item));
                    }
                } else {
                    alarmInfoList.add(CPResourceUtil.getAlarmInfoByCode(item));
                }
            }
        }

        List<Integer> tempAlarmCellNos = btgRealtimeData.alarmsCellNoMap.get(ConstantKit.TYPE_HIGH_TEMP_ALARM);
        if (tempAlarmCellNos != null && !tempAlarmCellNos.isEmpty()) {
            for (Integer cellNo : tempAlarmCellNos) {
                alarmInfoList.add(cellNo + "#" + CPResourceUtil.getAlarmInfoByCode(ConstantKit.TYPE_HIGH_TEMP_ALARM));
            }
        }

        tempAlarmCellNos = btgRealtimeData.alarmsCellNoMap.get(ConstantKit.TYPE_LOW_TEMP_ALARM);
        if (tempAlarmCellNos != null && !tempAlarmCellNos.isEmpty()) {
            for (Integer cellNo : tempAlarmCellNos) {
                alarmInfoList.add(cellNo + "#" + CPResourceUtil.getAlarmInfoByCode(ConstantKit.TYPE_LOW_TEMP_ALARM));
            }
        }

        if (alarmInfoList.isEmpty()) {
            tableNoDataAlarmInfo.setVisible(View.VISIBLE);
            recyclerViewAlaramInfo.setVisibility(View.GONE);
            tabAlm.setCount(R.drawable.circle_corner_f8cc14, 0);
        } else {
            alarmInfoAdapter.setData(alarmInfoList);
            tableNoDataAlarmInfo.setVisible(View.GONE);
            recyclerViewAlaramInfo.setVisibility(View.VISIBLE);
        }

        tabProtect.setCount(R.drawable.circle_corner_00ce80, 0);
        if (protects != null && !protects.isEmpty()) {
            tabProtect.setCount(R.drawable.circle_corner_00ce80, protects.size());
            for (Integer item : protects) {
                List<Integer> cellNumbers = btgRealtimeData.alarmsCellNoMap.get(item);
                if (cellNumbers != null && !cellNumbers.isEmpty()) {
                    for (Integer cellNo : cellNumbers) {
                        protectInfoList.add(cellNo + "#" + CPResourceUtil.getProtectInfoByCode(item));
                    }
                } else {
                    protectInfoList.add(CPResourceUtil.getProtectInfoByCode(item));
                }
            }
        }

        List<Integer> tempProtectCellNos = btgRealtimeData.protectsCellNoMap.get(ConstantKit.TYPE_HIGH_TEMP_PROTECT);
        if (tempProtectCellNos != null && !tempProtectCellNos.isEmpty()) {
            for (Integer cellNo : tempProtectCellNos) {
                protectInfoList.add(cellNo + "#" + CPResourceUtil.getProtectInfoByCode(ConstantKit.TYPE_HIGH_TEMP_PROTECT));
            }
        }

        tempProtectCellNos = btgRealtimeData.protectsCellNoMap.get(ConstantKit.TYPE_LOW_TEMP_PROTECT);
        if (tempProtectCellNos != null && !tempProtectCellNos.isEmpty()) {
            for (Integer cellNo : tempProtectCellNos) {
                protectInfoList.add(cellNo + "#" + CPResourceUtil.getProtectInfoByCode(ConstantKit.TYPE_LOW_TEMP_PROTECT));
            }
        }

        if (protectInfoList.isEmpty()) {
            tableNoDataProtectInfo.setVisible(View.VISIBLE);
            recyclerViewProtectInfo.setVisibility(View.GONE);
        } else {
            protectInfoAdapter.setData(protectInfoList);
            tableNoDataProtectInfo.setVisible(View.GONE);
            recyclerViewProtectInfo.setVisibility(View.VISIBLE);
        }

    }


    /**
     * 监听电池组实时数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowBtgRealtimeDataEvent(EventManager.OnShowBtgRealtimeDataEvent event) {
        try {
            handleRealTimeData(event.getBTGRealtimeData());
        } catch (Exception exception) {
            Log.d(TAG, "OnShowBtgRealtimeDataEvent: 实时告警页面 - 赋值报错");
        }
    }


    /**
     * 监听蓝牙断开成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDisconnectEvent(EventManager.OnBleDisconnectEvent event) {
        recyclerViewAlaramInfo.setVisibility(View.GONE);
        tableNoDataAlarmInfo.setVisible(View.VISIBLE);
        tabAlm.setCount(R.drawable.circle_corner_f8cc14, 0);

        recyclerViewProtectInfo.setVisibility(View.GONE);
        tableNoDataProtectInfo.setVisible(View.VISIBLE);
        tabProtect.setCount(R.drawable.circle_corner_00ce80, 0);
    }


    /**
     * 监听蓝牙连接成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectSuccessEvent(EventManager.OnBleConnectSuccessEvent event) {
        recyclerViewAlaramInfo.setVisibility(View.GONE);
        tableNoDataAlarmInfo.setVisible(View.VISIBLE);

        recyclerViewProtectInfo.setVisibility(View.GONE);
        tableNoDataProtectInfo.setVisible(View.VISIBLE);
    }


    /**
     * 监听蓝牙连接失败
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectFailedEvent(EventManager.OnBleConnectFailedEvent event) {
        recyclerViewAlaramInfo.setVisibility(View.GONE);
        tableNoDataAlarmInfo.setVisible(View.VISIBLE);
        tabAlm.setCount(R.drawable.circle_corner_f8cc14, 0);

        recyclerViewProtectInfo.setVisibility(View.GONE);
        tableNoDataProtectInfo.setVisible(View.VISIBLE);
        tabProtect.setCount(R.drawable.circle_corner_00ce80, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}