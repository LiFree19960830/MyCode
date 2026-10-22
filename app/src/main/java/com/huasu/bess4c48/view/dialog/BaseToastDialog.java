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
import com.huasu.bess4c48.utils.constant.BaseData;

/**
 * 统一的基础提示框
 */
public class BaseToastDialog extends BaseDialogFragment {
    private static final String INTENT_KEY_CONTENT = "intentKeyContent";
    private static final String INTENT_KEY_HIDE_BUTTON = "intentKeyHideButton";

    private TextView tvTitle;
    private TextView tvCancel;
    private TextView tvSure;
    private TextView tvContent;

    private OnViewClickListener mOnViewClickListener;

    public interface OnViewClickListener {
        void dismiss();

        void submit();
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
        // 设置view宽度，以及显示位置
        setViewAttributes(BaseData.ScreenWidth * 7 / 10, Gravity.CENTER, true);
        View root = inflater.inflate(R.layout.dialog_base_toast, container);
        tvCancel = root.findViewById(R.id.tvCancel);
        tvSure = root.findViewById(R.id.tvSure);
        tvTitle = root.findViewById(R.id.tvTitle);
        tvContent = root.findViewById(R.id.tvContent);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tvContent.setText(bundle.getString(INTENT_KEY_CONTENT));
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnViewClickListener != null)
                    mOnViewClickListener.dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnViewClickListener != null)
                    mOnViewClickListener.submit();
            }
        });
        return root;
    }


    //TODO app是全屏沉浸式的，发现activity在设置了全屏后，弹出dialog底部会跳出导航栏虚拟键。
    // 具体原因是因为Dialog的Window抢走了焦点，Window 中的 DecorView 状态改变导致了退出。
    @Override
    public void onStart() {
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.onStart();
        fullScreenImmersive(getDialog().getWindow().getDecorView());
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    private void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        fullScreenImmersive(getDialog().getWindow().getDecorView());
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * @param onViewClickListener
     * @param content             显示的内容
     * @return
     */
    public static BaseToastDialog newInstance(OnViewClickListener onViewClickListener, String content) {
        BaseToastDialog errorDialog = new BaseToastDialog();
        Bundle _bundle = new Bundle();
        _bundle.putString(INTENT_KEY_CONTENT, content);
        errorDialog.setArguments(_bundle);
        errorDialog.setOnViewClickListener(onViewClickListener);
        return errorDialog;
    }


    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
