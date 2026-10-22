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
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseDialogFragment;
import com.huasu.bess4c48.bean.SelectionData;
import com.huasu.bess4c48.utils.constant.BaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一的基础提示框
 */
public class DownloadDialog extends BaseDialogFragment {

    private TextView tv1, tv2, tv3, tv4, tvFile;
    private ImageView iv1, iv2, iv3, iv4, ivClose;
    private View view1, view2, view3;
    private List<ImageView> imgList = new ArrayList<>();
    private List<TextView> tvList = new ArrayList<>();
    private List<View> viewList = new ArrayList<>();

    private OnViewClickListener mListener;

    public interface OnViewClickListener {
        void dismiss();
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
        setViewAttributes(BaseData.ScreenWidth * 9 / 10, Gravity.CENTER, true);
        View root = inflater.inflate(R.layout.dialog_export, container);
        tv1 = root.findViewById(R.id.tv1);
        tv2 = root.findViewById(R.id.tv2);
        tv3 = root.findViewById(R.id.tv3);
        tv4 = root.findViewById(R.id.tv4);
        tvFile = root.findViewById(R.id.tvFile);
        iv1 = root.findViewById(R.id.iv1);
        iv2 = root.findViewById(R.id.iv2);
        iv3 = root.findViewById(R.id.iv3);
        iv4 = root.findViewById(R.id.iv4);
        ivClose = root.findViewById(R.id.ivClose);
        view1 = root.findViewById(R.id.view1);
        view2 = root.findViewById(R.id.view2);
        view3 = root.findViewById(R.id.view3);

        imgList.add(iv1);
        imgList.add(iv2);
        imgList.add(iv3);
        imgList.add(iv4);
        tvList.add(tv1);
        tvList.add(tv2);
        tvList.add(tv3);
        tvList.add(tv4);
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        setProgress(0);
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
    public static DownloadDialog newInstance(OnViewClickListener listener) {
        DownloadDialog errorDialog = new DownloadDialog();
        errorDialog.setOnGotoClickListener(listener);
        return errorDialog;
    }

    /**
     * 设置底部按钮点击事件
     *
     * @param listener
     * @return
     */
    private DownloadDialog setOnGotoClickListener(OnViewClickListener listener) {
        mListener = listener;
        return this;
    }

    public void setProgress(int progress) {
        tvList.get(progress).setTextColor(getResources().getColor(R.color.button_txt_color));
        imgList.get(progress).setImageDrawable(MyApplication.getInstance().getDrawable(R.mipmap.icon_finish));
        if (progress < viewList.size())
            viewList.get(progress).setBackgroundColor(getResources().getColor(R.color.button_txt_color));

        if ((progress + 1) < imgList.size())
            imgList.get(progress + 1).setImageDrawable(APNGDrawable.fromAsset(MyApplication.getInstance(), "icon_download.png"));
    }

    public void showExportFile(String file) {
        tvFile.setText(String.format(getString(R.string.export_tips5), file));
        ivClose.setVisibility(View.VISIBLE);
    }


    public void showExportFail() {
        tvFile.setText(getString(R.string.export_tips4));
        ivClose.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
