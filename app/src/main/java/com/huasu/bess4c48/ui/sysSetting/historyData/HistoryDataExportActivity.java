package com.huasu.bess4c48.ui.sysSetting.historyData;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseActivity;
import com.huasu.bess4c48.bean.CommandData;
import com.huasu.bess4c48.bean.HistoryData;
import com.huasu.bess4c48.bean.HistoryDataTemplate;
import com.huasu.bess4c48.bean.SelectionData;
import com.huasu.bess4c48.callback.MyUiCallback;
import com.huasu.bess4c48.event.EventManager;
import com.huasu.bess4c48.utils.ByteUtils;
import com.huasu.bess4c48.utils.CPResourceUtil;
import com.huasu.bess4c48.utils.DataUtils;
import com.huasu.bess4c48.utils.FileUtils;
import com.huasu.bess4c48.utils.ToolUtil;
import com.huasu.bess4c48.view.dialog.BaseToastDialog;
import com.huasu.bess4c48.view.dialog.DownloadDialog;
import com.huasu.bess4c48.view.dialog.SelectionOneDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryDataExportActivity extends BaseActivity {

    private SelectionOneDialog selectionOneDialog;
    private BaseToastDialog toastDialog;
    private DownloadDialog downloadDialog;
    private int searchCount, readCount, readIndex = 1;
    private ArrayList<SelectionData> selectionDataArrayList;
    private TextView tvSearchCount, tvFile;
    private Button btnExport;
    private List<HistoryDataTemplate> mTemplateList = new ArrayList<>();
    private String generate, clear;
    private List<HistoryData> mHistoryDataList = new ArrayList<>();

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        generate = getString(R.string.generate_event);
        clear = getString(R.string.clear_event);
        btnExport = findViewById(R.id.btnExport);
        tvSearchCount = findViewById(R.id.tvSearchCount);
        tvFile = findViewById(R.id.tvFile);
        init();
        tvSearchCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBluetoothConnected(true)) {
                    if (searchCount <= 0) {
                        MyApplication.showToastFailed(getString(R.string.select_export_count));
                        return;
                    }
                    MyApplication.showLoadingDialog(mActivity, getString(R.string.search_data_count), 2000);
                    MyUiCallback.getInstance().sendReadCommand(CommandData.command_type_read_history_data_count, (byte) 0x01, (byte) 0x39, 1, "查询历史记录总条数", false);
                }
            }
        });


    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_export_history_data;
    }

    @Override
    protected boolean isTitleBar() {
        return true;
    }

    @Override
    protected String getTitleMsg() {
        return getString(R.string.setting_data_export);
    }

    private void init() {
        selectionDataArrayList = new ArrayList<>();
        selectionDataArrayList.add(new SelectionData("100", "100"));
        selectionDataArrayList.add(new SelectionData("200", "200"));
        selectionDataArrayList.add(new SelectionData("300", "300"));
        selectionDataArrayList.add(new SelectionData("400", "400"));
        selectionDataArrayList.add(new SelectionData("500", "500"));
        selectionDataArrayList.add(new SelectionData("65535", getString(R.string.all)));
        mTemplateList.add(new HistoryDataTemplate(0.01f, 0, 1, 4, "setTotalV"));//总压
        mTemplateList.add(new HistoryDataTemplate(0.1f, 30000, 1, 5, "setTotalC"));//电流
        mTemplateList.add(new HistoryDataTemplate(0.1f, 0, 1, 6, "setSoc"));//soc
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 7, "setCellV1"));//电压1
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 8, "setCellV2"));//电压2
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 9, "setCellV3"));//电压3
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 10, "setCellV4"));//电压4
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 11, "setCellV5"));//电压5
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 12, "setCellV6"));//电压6
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 13, "setCellV7"));//电压7
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 14, "setCellV8"));//电压8
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 15, "setCellV9"));//电压9
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 16, "setCellV10"));//电压10
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 17, "setCellV11"));//电压11
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 18, "setCellV12"));//电压12
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 19, "setCellV13"));//电压13
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 20, "setCellV14"));//电压14
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 21, "setCellV15"));//电压15
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 22, "setCellV16"));//电压16
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 23, "setCellT1"));//温度1
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 24, "setCellT2"));//温度2
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 25, "setCellT3"));//温度3
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 26, "setCellT4"));//温度4
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 27, "setMaxCellV"));//最高单体电压
        mTemplateList.add(new HistoryDataTemplate(0.001f, 0, 3, 28, "setMinCellV"));//最低单体电压
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 29, "setMosT"));//Mos温度
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 30, "setAmbientT"));//环境温度
        mTemplateList.add(new HistoryDataTemplate(1f, 0, 0, 31, "setMaxVoltDiff"));//电池压差
        mTemplateList.add(new HistoryDataTemplate(1f, 0, 0, 32, "setDCC"));//循环次数
        mTemplateList.add(new HistoryDataTemplate(0.1f, 0, 1, 37, "setSoh"));//SOH
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 38, "setCellT5"));//温度5
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 39, "setCellT6"));//温度6
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 40, "setCellT7"));//温度7
        mTemplateList.add(new HistoryDataTemplate(0.1f, 500, 1, 41, "setCellT8"));//温度8
        mTemplateList.add(new HistoryDataTemplate(1f, 0, 0, 42, "setIPS"));//IO引脚状态
        mTemplateList.add(new HistoryDataTemplate(0.01f, 0, 2, 43, "setRCC"));//请求充电电流
        mTemplateList.add(new HistoryDataTemplate(1f, 0, 0, 48, "setEAPRV"));//额外告警保护寄存器值
        mTemplateList.add(new HistoryDataTemplate(1f, 0, 0, 49, "setFS"));//故障汇总
    }

    private void showSelectDialog() {
        selectionOneDialog = SelectionOneDialog.newInstance(selectionDataArrayList, getString(R.string.select_export_count), -1, new SelectionOneDialog.OnViewClickListener() {
            @Override
            public void onSelect(int checkPosition) {

            }

            @Override
            public void dismiss() {
                selectionOneDialog = null;
            }

            @Override
            public void onSure(int checkPosition, SelectionData checkedItem) {
                if (checkedItem != null) {
                    tvSearchCount.setText(checkedItem.text);
                    searchCount = Integer.parseInt(checkedItem.value);
                }
            }
        });
        selectionOneDialog.show(getSupportFragmentManager(), getClass().getSimpleName());

    }

    private void showToastDialog() {
        String tips;
        if (searchCount == 65535)
            searchCount = readCount;
        if (readCount < searchCount)
            tips = String.format(getString(R.string.export_tips1), String.valueOf(readCount), String.valueOf(searchCount), String.valueOf(readCount));
        else
            tips = String.format(getString(R.string.export_tips2), String.valueOf(searchCount));
        toastDialog = BaseToastDialog.newInstance(new BaseToastDialog.OnViewClickListener() {
            @Override
            public void dismiss() {
                toastDialog = null;
            }

            @Override
            public void submit() {
                toastDialog = null;
                tvFile.setVisibility(View.GONE);
                readIndex = 1;
                showDownloadDialog();
                startRead();
            }
        }, tips);
        toastDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
    }

    private void showFileDialog() {
        if (downloadDialog != null)
            downloadDialog.dismiss();
        toastDialog = BaseToastDialog.newInstance(new BaseToastDialog.OnViewClickListener() {
            @Override
            public void dismiss() {
                toastDialog = null;
            }

            @Override
            public void submit() {
                toastDialog = null;
                openFileDirectoryBySystem();
            }
        }, getString(R.string.export_tips3));
        toastDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
    }


    private void showDownloadDialog() {
        if (downloadDialog != null) {
            downloadDialog.dismiss();
            downloadDialog = null;
        }
        downloadDialog = DownloadDialog.newInstance(new DownloadDialog.OnViewClickListener() {
            @Override
            public void dismiss() {
                downloadDialog = null;
            }
        });
        downloadDialog.show(getSupportFragmentManager(), getClass().getSimpleName());
    }

    private void startRead() {
        if (readIndex <= (readCount < searchCount ? readCount : searchCount))
            MyUiCallback.getInstance().sendReadHistoryCommand(readIndex, "读取历史数据", false);
        else {
            //读取完成，更新状态，开始导出
            downloadDialog.setProgress(1);
            exportRecordData();
        }
    }

    File exportFile;

    /**
     * 导出历史记录数据
     *
     * @return
     */
    public void exportRecordData() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                if (mHistoryDataList != null && !mHistoryDataList.isEmpty()) {
                    String CSV_COLUMN_SEPARATOR = ",", CSV_ROW_SEPARATOR = "\r\n";
                    //TODO 1.查询需要导出的数据
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getDefault());
                    //TODO 2.设置导出文件前缀
                    String fName = "historyData_" + sdf.format(new Date()) + ".csv";
                    String path = FileUtils.getExportDataPath();
                    exportFile = new File(path, fName);
                    //TODO 3.文件导出
                    try {
                        StringBuffer buf = new StringBuffer();
                        String[] titleArr = getResources().getStringArray(R.array.export_columns);
                        String[] fieldArr = getResources().getStringArray(R.array.export_columns_field);
                        //TODO 2.组装表头
                        for (String title : titleArr) {
                            buf.append(title).append(CSV_COLUMN_SEPARATOR);
                        }
                        buf.deleteCharAt(buf.length() - 1);
                        buf.append(CSV_ROW_SEPARATOR);
                        //TODO 4.组装数据
                        Method method;
                        for (HistoryData item : mHistoryDataList) {
                            for (String field : fieldArr) {
                                method = item.getClass().getMethod(field);
                                buf.append(method.invoke(item)).append(CSV_COLUMN_SEPARATOR);
                            }
                            buf.deleteCharAt(buf.length() - 1);
                            buf.append(CSV_ROW_SEPARATOR);
                        }
                        //导出数据准备完成，开始导出
                        emitter.onNext(3);
                        byte[] content = buf.toString().getBytes("UTF-8");
                        OutputStream os = new FileOutputStream(exportFile);
                        os.write(new byte[]{(byte) 0XEF, (byte) 0XBB, (byte) 0XBF});
                        //TODO 5.写出响应
                        os.write(content);
                        os.flush();
                        os.close();
                        //导出完成
                        emitter.onNext(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        emitter.onNext(0);
                    }
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer result) {
                        if (result == 1) {//导出成功
                            tvFile.setVisibility(View.VISIBLE);
                            tvFile.setText(String.format(getString(R.string.export_tips5), exportFile.getPath()));
                            downloadDialog.setProgress(3);
                            downloadDialog.showExportFile(exportFile.getPath());
//                            showFileDialog();
                        }
                        if (result == 3) {//导出数据准备完成，开始导出
                            downloadDialog.setProgress(2);
                        }
                        if (result == 0) {//导出失败
                            tvFile.setVisibility(View.VISIBLE);
                            tvFile.setText(getString(R.string.export_tips4));
                            downloadDialog.setProgress(3);
                            downloadDialog.showExportFail();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void openFileDirectoryBySystem() {
        if (!exportFile.exists()) {
            return;
        }


        Uri treeUri = Uri.parse("content://" + FileUtils.getExportDataPath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(treeUri, "*/*");
        startActivity(intent); // 旧的方法，不推荐使用，改用下面的方式
        // 使用ActivityResultLauncher的新方法（推荐）
    }


    /**
     * 收到读取参数的返回值
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowDeviceParamEvent(EventManager.OnShowDeviceParamEvent event) {
        if (event.getReceiveArray() != null && isCurrentActivity() && event.getCommandType() == CommandData.command_type_read_history_data_count) {
            readCount = DataUtils.getNum(event.getReceiveArray()[0], event.getReceiveArray()[1]);
            showToastDialog();
        }
    }

    /**
     * 收到历史数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowHistoryDataEvent(EventManager.OnShowHistoryDataEvent event) {
        if (event.getReceiveArray() != null && isCurrentActivity()) {
            byte[] receiveArray = event.getReceiveArray();
            HistoryData historyData = new HistoryData();
            Class<? extends HistoryData> clazz = historyData.getClass();
            historyData.updateTime = ToolUtil.convertHexToVal(ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[0], receiveArray[1]
                    , receiveArray[2], receiveArray[3]}), 1f, 0, 0);
            historyData.updateTime = ToolUtil.formatDate(Long.parseLong(historyData.updateTime) * 1000);
            setAddStatus(historyData, DataUtils.getNum(receiveArray[4], receiveArray[5]));//新增状态
            int eventStatus = DataUtils.getNum(receiveArray[6], receiveArray[7]);//事件
            if (eventStatus >= 32768) {//大于表示事件或者告警消除，需要减去8000，代表具体的事件或者告警类型
                eventStatus = eventStatus - 8000;
                historyData.event = String.format(clear, CPResourceUtil.getResStringById("event_" + eventStatus));
            } else {
                historyData.event = CPResourceUtil.getResStringById("event_" + eventStatus);
            }
            try {
                historyData.CTC = String.valueOf(DataUtils.getNum(receiveArray[68], receiveArray[69], receiveArray[66], receiveArray[67]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                historyData.DTC = String.valueOf(DataUtils.getNum(receiveArray[72], receiveArray[73], receiveArray[70], receiveArray[71]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                historyData.ARV = String.valueOf(DataUtils.getNum(receiveArray[90], receiveArray[91], receiveArray[88], receiveArray[89]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                historyData.PRV = String.valueOf(DataUtils.getNum(receiveArray[94], receiveArray[95], receiveArray[92], receiveArray[93]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            historyData.CCR = event.isCrcResult() ? getString(R.string.crc_result) : getString(R.string.crc_result_no);
            Method method;
            for (HistoryDataTemplate item : mTemplateList) {
                try {
                    Log.e("setDCC", item.method + ";" + getHexVal(receiveArray, item.position));
                    method = clazz.getMethod(item.method, String.class);
                    method.invoke(historyData, ToolUtil.convertHexToVal(
                            getHexVal(receiveArray, item.position), item.zoom, item.offsetVal, item.precision)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mHistoryDataList.add(historyData);
            //开始读取下一条记录
            readIndex++;
            startRead();
        }
    }


    /**
     * 收到历史数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnShowHistoryDataFailEvent(EventManager.OnShowHistoryDataFailEvent event) {
        if (isCurrentActivity()) {
            //开始读取下一条记录
            readIndex++;
            startRead();
        }
    }

    private void setAddStatus(HistoryData historyData, int addStatus) {
        int alarmStatus = (addStatus & (1 << 0)) != 0 ? 1 : 0;//告警状态
        int procStatus = (addStatus & (1 << 1)) != 0 ? 1 : 0;//保护状态
        historyData.faultStatus = CPResourceUtil.getResStringById("alarm_status_" + alarmStatus) +
                "/" + CPResourceUtil.getResStringById("proc_status_" + procStatus);
        historyData.CM = CPResourceUtil.getResStringById("mos_status_" + ((addStatus & (1 << 2)) != 0 ? 1 : 0));
        historyData.DCM = CPResourceUtil.getResStringById("mos_status_" + ((addStatus & (1 << 3)) != 0 ? 1 : 0));
        historyData.HM = CPResourceUtil.getResStringById("mos_status_" + ((addStatus & (1 << 4)) != 0 ? 1 : 0));
        historyData.CLM = CPResourceUtil.getResStringById("mos_status_" + ((addStatus & (1 << 5)) != 0 ? 1 : 0));
        historyData.BS = CPResourceUtil.getResStringById("balance_status_" + ((addStatus & (1 << 6)) != 0 ? 1 : 0));
        historyData.CDS = CPResourceUtil.getResStringById("charge_state_" + ((addStatus & (1 << 7)) != 0 ? 1 : 0));
    }

    public static void main(String[] args) {
        HistoryData historyData = new HistoryData();
        Class<? extends HistoryData> clazz = historyData.getClass();
        try {
            Method method = clazz.getMethod("setTotalV", String.class);
            method.invoke(historyData, "300");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("data=" + historyData.totalV);
    }

    public String getHexVal(byte[] receiveArray, int position) {
        return ByteUtils.byteArrTo2Hex(new byte[]{receiveArray[position * 2], receiveArray[position * 2 + 1]});
    }

    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, HistoryDataExportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }
}
