package com.huasu.bess4c48.ui.realtimeData;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.NonUiContext;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.adapter.CellVoltageAdapter;
import com.huasu.bess4c48.bean.BTGRealtimeData;
import com.huasu.bess4c48.bean.BTRealtimeData;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.SpaceItemDecorationHor;
import com.huasu.bess4c48.utils.ToolUtil;
import com.huasu.bess4c48.view.TableNoData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.List;

/**
 * 实时数据
 */
public class RealTimeDataFragment extends Fragment {

    private static final String TAG = "RealTimeDataFragment";


    private TextView tvGuardBoardTime;
    private TextView tvTotalVoltage;
    private TextView tvCurrent;
    private TextView tvSoc;
    private TextView tvSoh;
    private TextView tvCapacity;
    private TextView tvLowestBtVoltage;
    private TextView tvHighBtVoltage;
    private TextView tvVoltageRange;
    private TextView tvChargeCycleIndex;
    private TextView tvDisChargeCycleIndex;

    private TextView tvEnviromentTemperature;
    private TextView tvMosTemperature;
    private TextView tvT1Temp, tvT2TemP, tvT3TemP, tvT4Temp, tvT5Temp, tvT6Temp, tvT7Temp, tvT8Temp;
    private LinearLayout ll5, ll6;

    private CellVoltageAdapter cellVoltageAdapter;

    private RecyclerView rvCellVoltage;

    private TableNoData tableNoDataCellVoltage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realtime_data, container, false);
        initView(view);
        EventBus.getDefault().register(this);
        return view;
    }

    /**
     * 获取页面元素
     */
    public void initView(View view) {

        tableNoDataCellVoltage = view.findViewById(R.id.tableNoDataCellVoltage);
//        tvSoftwareVersion = view.findViewById(R.id.tvSoftwareVersion);
        tvGuardBoardTime = view.findViewById(R.id.tvGuardBoardTime);
        tvTotalVoltage = view.findViewById(R.id.tvTotalVoltage);
        tvCurrent = view.findViewById(R.id.tvCurrent);
        tvSoc = view.findViewById(R.id.tvSoc);
        tvSoh = view.findViewById(R.id.tvSoh);
        tvCapacity = view.findViewById(R.id.tvCapacity);
        tvLowestBtVoltage = view.findViewById(R.id.tvLowestBtVoltage);
        tvHighBtVoltage = view.findViewById(R.id.tvHighBtVoltage);
        tvVoltageRange = view.findViewById(R.id.tvVoltageRange);
        tvChargeCycleIndex = view.findViewById(R.id.tvChargeCycleIndex);
        tvDisChargeCycleIndex = view.findViewById(R.id.tvDisChargeCycleIndex);

        tvEnviromentTemperature = view.findViewById(R.id.tvEnviromentTemperature);
        tvMosTemperature = view.findViewById(R.id.tvMosTemperature);
        tvT1Temp = view.findViewById(R.id.tvT1Temperature);
        tvT2TemP = view.findViewById(R.id.tvT2Temperature);
        tvT3TemP = view.findViewById(R.id.tvT3Temperature);
        tvT4Temp = view.findViewById(R.id.tvT4Temperature);
        tvT5Temp = view.findViewById(R.id.tvT5Temperature);
        tvT6Temp = view.findViewById(R.id.tvT6Temperature);
        tvT7Temp = view.findViewById(R.id.tvT7Temperature);
        tvT8Temp = view.findViewById(R.id.tvT8Temperature);

        ll5 = view.findViewById(R.id.ll5);
        ll6 = view.findViewById(R.id.ll6);

        rvCellVoltage = view.findViewById(R.id.rvCellVoltage);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rvCellVoltage.setLayoutManager(layoutManager);
        rvCellVoltage.addItemDecoration(new SpaceItemDecorationHor(0, 0));
        cellVoltageAdapter = new CellVoltageAdapter();
        rvCellVoltage.setAdapter(cellVoltageAdapter);
    }


    /**
     * 清空页面数据
     */
    public void clearPageData() {
        // 软件版本/保护板时间赋值
//        tvSoftwareVersion.setText("--");
        tvGuardBoardTime.setText("--");

        // 数据总览赋值
        tvTotalVoltage.setText("--");
        tvCurrent.setText("--");
        tvSoc.setText("--");
        tvSoh.setText("--");
        tvCapacity.setText("--");
        tvLowestBtVoltage.setText("--");
        tvHighBtVoltage.setText("--");
        tvVoltageRange.setText("--");
        tvChargeCycleIndex.setText("--");
        tvDisChargeCycleIndex.setText("--");

        // 温度情况赋值
        tvEnviromentTemperature.setText("--");
        tvMosTemperature.setText("--");
        tvT1Temp.setText("--");
        tvT2TemP.setText("--");
        tvT3TemP.setText("--");
        tvT4Temp.setText("--");

        tableNoDataCellVoltage.setVisible(View.VISIBLE);
        rvCellVoltage.setVisibility(View.GONE);
    }


    /**
     * 监听电池组实时数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowBtgRealtimeDataEvent(EventManager.OnShowBtgRealtimeDataEvent event) {
        // 获取电池组数据
        BTGRealtimeData btgRealtimeData = event.getBTGRealtimeData();

        // 软件版本/保护板时间赋值
        // tvSoftwareVersion.setText(btgRealtimeData.diff);

        tvGuardBoardTime.setText(ToolUtil.formatDate(btgRealtimeData.reportTime));

        // 数据总览赋值
        tvTotalVoltage.setText(btgRealtimeData.getVolt());
        tvCurrent.setText(btgRealtimeData.getCurrent());
        tvSoc.setText(String.valueOf(btgRealtimeData.soc));
        tvSoh.setText(String.valueOf(btgRealtimeData.soh));
        tvCapacity.setText(String.valueOf(btgRealtimeData.capacity));
        tvLowestBtVoltage.setText(btgRealtimeData.getMinVolt());
        tvHighBtVoltage.setText(btgRealtimeData.getMaxVolt());
        tvVoltageRange.setText(String.valueOf(btgRealtimeData.rangeValue));
        tvChargeCycleIndex.setText(btgRealtimeData.getChgCyclesString());
        tvDisChargeCycleIndex.setText(btgRealtimeData.getDisChgCyclesString());

        // 电芯电压赋值
        tableNoDataCellVoltage.setVisible(View.GONE);
        rvCellVoltage.setVisibility(View.VISIBLE);
        cellVoltageAdapter.setData(btgRealtimeData.btRealtimeDataList);

        // 温度情况赋值
        tvEnviromentTemperature.setText(String.valueOf(btgRealtimeData.envTemp));
        tvMosTemperature.setText(String.valueOf(btgRealtimeData.mosTemp));

        if (btgRealtimeData.btRealtimeDataList != null) {

            setTempToView(tvT1Temp, btgRealtimeData.btRealtimeDataList, 0);
            setTempToView(tvT2TemP, btgRealtimeData.btRealtimeDataList, 1);
            setTempToView(tvT3TemP, btgRealtimeData.btRealtimeDataList, 2);
            setTempToView(tvT4Temp, btgRealtimeData.btRealtimeDataList, 3);
            if (btgRealtimeData.tempNum > 4) {
                ll5.setVisibility(View.VISIBLE);
                tvT5Temp.setText(btgRealtimeData.getBtTemp5());
                tvT6Temp.setText(btgRealtimeData.getBtTemp6());
            }
            if (btgRealtimeData.tempNum > 6) {
                ll6.setVisibility(View.VISIBLE);
                tvT7Temp.setText(btgRealtimeData.getBtTemp7());
                tvT8Temp.setText(btgRealtimeData.getBtTemp8());
            }
        }

    }

    private void setTempToView(TextView tv, List<BTRealtimeData> list, int index) {
        if (index < list.size()) {
            BigDecimal T = list.get(index).monomerTemperature;
            if (T == null)
                tv.setText("--");
            else
                tv.setText(String.valueOf(T));
        }
    }

    /**
     * 监听蓝牙断开成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDisconnectEvent(EventManager.OnBleDisconnectEvent event) {
        clearPageData();
    }


    /**
     * 监听蓝牙连接成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectSuccessEvent(EventManager.OnBleConnectSuccessEvent event) {
        clearPageData();
    }


    /**
     * 监听蓝牙连接失败
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectFailedEvent(EventManager.OnBleConnectFailedEvent event) {
        clearPageData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}