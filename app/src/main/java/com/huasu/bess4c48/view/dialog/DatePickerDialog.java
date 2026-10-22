package com.huasu.bess4c48.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseDialogFragment;
import com.huasu.bess4c48.bean.SelectDate;
import com.huasu.bess4c48.utils.ToolUtil;
import com.huasu.bess4c48.view.DatePickerView;

import java.util.Calendar;

public class DatePickerDialog extends BaseDialogFragment {
    private static final String INTENT_KEY_YEAR = "intentKeyYear";
    private static final String INTENT_KEY_MONTH = "intentKeyMonth";
    private static final String INTENT_KEY_DAY = "intentKeyDay";
    private static final String INTENT_KEY_HOUR = "intentKeyHour";
    private static final String INTENT_KEY_MINUTE = "intentKeyMinute";
    private static final String INTENT_KEY_SECOND = "intentKeySecond";
    private static final String INTENT_KEY_SHOW_SECOND = "intentKeyShowSecond";
    private static final String INTENT_KEY_SHOW_MINUTE = "intentKeyShowMinute";
    private static final String INTENT_KEY_SELECTDATE = "intentKeySelectDate";
    private static final String INTENT_KEY_MONTHAGO = "intentKeyMonthAgo";
    DatePickerView mDatePickerView;
    private TextView tvCancel, tvSure;
    private OnViewClickListener mListener;
    private SelectDate selectDate;
    private boolean isShowSecond = false, isShowMinute = true;
    private int monthAgo = 0;

    public interface OnViewClickListener {
        void onSelect(int checkPosition);

        void dismiss();

        void onSure(SelectDate selectDate);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setViewAttributes(WindowManager.LayoutParams.MATCH_PARENT, 800, Gravity.BOTTOM, true);
        View root = inflater.inflate(R.layout.dialog_change_date, container);
        mDatePickerView = root.findViewById(R.id.datePickerView);
        tvSure = root.findViewById(R.id.tvSure);
        tvCancel = root.findViewById(R.id.tvCancel);
        Calendar calendar = ToolUtil.getLastYear(1);
        int defaultYear = calendar.get(Calendar.YEAR);
        int defaultMonth = calendar.get(Calendar.MONTH) + 1;
        int defaultDay = calendar.get(Calendar.DATE) + 1;
        int defaultHour = calendar.get(Calendar.HOUR_OF_DAY);
        int defaultMinute = calendar.get(Calendar.MINUTE);
        int defaultSecond = calendar.get(Calendar.SECOND);
        selectDate = new SelectDate(defaultYear, defaultMonth, defaultDay, defaultHour, defaultMinute, defaultSecond);
        Bundle bundle = getArguments();
        if (bundle != null) {
            SelectDate newSelectDate = (SelectDate) bundle.getSerializable(INTENT_KEY_SELECTDATE);
            selectDate.year = newSelectDate.year;
            selectDate.month = newSelectDate.month;
            selectDate.day = newSelectDate.day;
            selectDate.hour = newSelectDate.hour;
            selectDate.minute = newSelectDate.minute;
            selectDate.second = newSelectDate.second;
            isShowSecond = bundle.getBoolean(INTENT_KEY_SHOW_SECOND, false);
            isShowMinute = bundle.getBoolean(INTENT_KEY_SHOW_MINUTE, true);
            monthAgo = bundle.getInt(INTENT_KEY_MONTHAGO, 3);
        }
        mDatePickerView.setDate(selectDate, true, isShowMinute, isShowSecond, monthAgo);

        mDatePickerView.addOnSelectedChangingListener(new DatePickerView.OnSelectedChangedListener() {
            @Override
            public void OnSelectedChanged(int[] oldValue, int[] newValue) {
                selectDate.updateData(newValue);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null)
                    mListener.dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null)
                    mListener.onSure(selectDate);
            }
        });
        setCancelable(true);
        return root;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null)
            mListener.dismiss();
    }

    public static DatePickerDialog newInstance(SelectDate selectDate, OnViewClickListener clickListener) {
        DatePickerDialog errorDialog = new DatePickerDialog();
        Bundle _bundle = new Bundle();
        _bundle.putSerializable(INTENT_KEY_SELECTDATE, selectDate);
        errorDialog.setArguments(_bundle);
        errorDialog.setOnGotoClickListener(clickListener);
        return errorDialog;
    }

    public static DatePickerDialog newInstance(SelectDate selectDate, boolean isShowSecond, OnViewClickListener clickListener) {
        DatePickerDialog errorDialog = new DatePickerDialog();
        Bundle _bundle = new Bundle();
        _bundle.putSerializable(INTENT_KEY_SELECTDATE, selectDate);
        _bundle.putBoolean(INTENT_KEY_SHOW_SECOND, isShowSecond);
        errorDialog.setArguments(_bundle);
        errorDialog.setOnGotoClickListener(clickListener);
        return errorDialog;
    }

    public static DatePickerDialog newInstance(SelectDate selectDate, boolean isShowSecond, int monthAgo, OnViewClickListener clickListener) {
        DatePickerDialog errorDialog = new DatePickerDialog();
        Bundle _bundle = new Bundle();
        _bundle.putSerializable(INTENT_KEY_SELECTDATE, selectDate);
        _bundle.putBoolean(INTENT_KEY_SHOW_SECOND, isShowSecond);
        _bundle.putInt(INTENT_KEY_MONTHAGO, monthAgo);
        errorDialog.setArguments(_bundle);
        errorDialog.setOnGotoClickListener(clickListener);
        return errorDialog;
    }

    public static DatePickerDialog newInstance(SelectDate selectDate, boolean isShowMinute, boolean isShowSecond, OnViewClickListener clickListener) {
        DatePickerDialog errorDialog = new DatePickerDialog();
        Bundle _bundle = new Bundle();
        _bundle.putSerializable(INTENT_KEY_SELECTDATE, selectDate);
        _bundle.putBoolean(INTENT_KEY_SHOW_SECOND, isShowSecond);
        _bundle.putBoolean(INTENT_KEY_SHOW_MINUTE, isShowMinute);
        errorDialog.setArguments(_bundle);
        errorDialog.setOnGotoClickListener(clickListener);
        return errorDialog;
    }

    /**
     * 设置底部按钮点击事件
     *
     * @param listener
     * @return
     */
    private DatePickerDialog setOnGotoClickListener(OnViewClickListener listener) {
        mListener = listener;
        return this;
    }


    @Override
    public void onDestroyView() {
        mListener = null;
        super.onDestroyView();
    }
}
