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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseDialogFragment;
import com.huasu.bess4c48.utils.constant.BaseData;

/**
 * 升级弹框
 */
public class UpgradeDialog extends BaseDialogFragment {

    private TextView tvResult;
    private ImageView iv1, ivClose;
    private LinearLayout llUpgrade;


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
        setViewAttributes(BaseData.ScreenWidth * 9 / 10, Gravity.CENTER, true);
        View root = inflater.inflate(R.layout.dialog_upgrade, container);
        tvResult = root.findViewById(R.id.tvResult);
        iv1 = root.findViewById(R.id.iv1);
        ivClose = root.findViewById(R.id.ivClose);
        llUpgrade = root.findViewById(R.id.llUpgrade);
        iv1.setImageDrawable(APNGDrawable.fromAsset(getContext(), "icon_upgrade.png"));

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
     * @return
     */
    public static UpgradeDialog newInstance() {
        UpgradeDialog errorDialog = new UpgradeDialog();
        return errorDialog;
    }


    public void showExportFile(String result) {
        tvResult.setText(String.format(getString(R.string.upgrade_tips2), result));
        ivClose.setVisibility(View.VISIBLE);
        llUpgrade.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
