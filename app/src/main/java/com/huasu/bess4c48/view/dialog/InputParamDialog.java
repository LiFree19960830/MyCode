package com.huasu.bess4c48.view.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseDialogFragment;
import com.huasu.bess4c48.bean.DeviceParam;

import java.math.BigDecimal;


public class InputParamDialog extends BaseDialogFragment {

    private static final String TAG = "InputParamDialog";
    private static final String INTENT_KEY_PARAM = "intentKeyParam";
    private static final String INTENT_KEY_PARENT_PARAM = "intentKeyParentParam";

    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvSure;
    private TextView tvRange;
    private EditText etParam;
    private DeviceParam mDeviceParam;
    private OnViewClickListener mListener;


    public interface OnViewClickListener {
        void onSure(String inputVal);

        void dismiss();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setCancelable(false);
        //去掉dialog的标题，需要在setContentView()之前
        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getDialog().getWindow();
        //去掉dialog默认的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        // 下面这句一定要加, xml中需要给弹窗设置一个背景色
        //  params.windowAnimations = R.style.BottomDialogAnimation;
        window.setBackgroundDrawable(new ColorDrawable());
        View root = inflater.inflate(R.layout.dialog_input_param, null);
        tvTitle = root.findViewById(R.id.tvTitle);
        tvCancel = root.findViewById(R.id.tvCancel);
        tvRange = root.findViewById(R.id.tvRange);
        tvSure = root.findViewById(R.id.tvSure);
        etParam = root.findViewById(R.id.etParam);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mDeviceParam = (DeviceParam) bundle.getSerializable(INTENT_KEY_PARAM);
            tvTitle.setText(bundle.getString(INTENT_KEY_PARENT_PARAM) + "-" + mDeviceParam.paramName);
            etParam.setText(mDeviceParam.getConvertVal());
            etParam.setInputType(mDeviceParam.precision > 0 ? InputType.TYPE_NUMBER_FLAG_DECIMAL :
                    InputType.TYPE_CLASS_NUMBER);
            tvRange.setText(mDeviceParam.getRangeNew());
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        return root;

    }


    public static InputParamDialog newInstance(DeviceParam param, String parentName, OnViewClickListener clickListener) {
        InputParamDialog errorDialog = new InputParamDialog();
        errorDialog.setClickListener(clickListener);
        Bundle _bundle = new Bundle();
        _bundle.putSerializable(INTENT_KEY_PARAM, param);
        _bundle.putString(INTENT_KEY_PARENT_PARAM, parentName);
        errorDialog.setArguments(_bundle);
        return errorDialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void submit() {
        if (TextUtils.isEmpty(etParam.getText()))
            return;
        try {
            if (!TextUtils.isEmpty(mDeviceParam.minVal) && !TextUtils.isEmpty(mDeviceParam.maxVal)) {
                BigDecimal minParam = new BigDecimal(mDeviceParam.minVal);
                BigDecimal maxParam = new BigDecimal(mDeviceParam.maxVal);
                BigDecimal inputVal = new BigDecimal(etParam.getText().toString());
                if (inputVal.compareTo(minParam) == -1 || inputVal.compareTo(maxParam) == 1) {
                    MyApplication.getInstance().showToastFailed(getString(R.string.toast_input_right_val));
                    return;
                }
            }
        }catch (Exception e){
            MyApplication.getInstance().showToastFailed(getString(R.string.toast_input_right_val));
            return;
        }

        dismiss();
        if (mListener != null)
            mListener.onSure(etParam.getText().toString());
    }

    public void setClickListener(OnViewClickListener clickListener) {
        this.mListener = clickListener;
    }
}
