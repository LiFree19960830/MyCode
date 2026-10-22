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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.adapter.SelectionOneDialogAdapter;
import com.huasu.bess4c48.base.BaseDialogFragment;
import com.huasu.bess4c48.bean.SelectionData;
import com.huasu.bess4c48.utils.ToolUtil;

import java.util.ArrayList;


/**
 * 选中一项的弹窗
 */
public class SelectionOneDialog extends BaseDialogFragment {
    private static final String INTENT_KEY_SELECTIONDATA = "intentKeySelectionData";
    private static final String INTENT_KEY_TITLE = "intentKeyTitle";
    private static final String INTENT_KEY_CHECK_POSITION = "intentKeyCheckPosition";
    private static final String INTENT_KEY_DIALOG_HEIGHT = "intentKeyDiaglogHeight";
    private static final String INTENT_KEY_SHOW_CHECK_DEFAULT = "intentKeyShowCheckDefault";


    private LinearLayout mLlSelectionWrapper;
    private TextView mTvSelTitle;
    private TextView tvCancel;
    private TextView tvSure;
    private RecyclerView mRlView;
    private OnViewClickListener mListener;
    private ArrayList<SelectionData> list;
    private SelectionOneDialogAdapter mAdapter;
    private int mCheckedPosition = 0;
    private boolean isShowCheckDefault = false;//是否显示低高选择按钮

    public interface OnViewClickListener {
        void onSelect(int checkPosition);

        void dismiss();

        void onSure(int checkPosition, SelectionData checkedItem);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.dialog_selection, null);

        initView(root);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTvSelTitle.setText(bundle.getString(INTENT_KEY_TITLE));
            if (list != null) {
                list.clear();
            }
            list = bundle.getParcelableArrayList(INTENT_KEY_SELECTIONDATA);
            mCheckedPosition = bundle.getInt(INTENT_KEY_CHECK_POSITION, 0);
            int height = bundle.getInt(INTENT_KEY_DIALOG_HEIGHT, ToolUtil.dp2px(280));
            isShowCheckDefault = bundle.getBoolean(INTENT_KEY_SHOW_CHECK_DEFAULT, false);
            setViewAttributes(WindowManager.LayoutParams.MATCH_PARENT, height, Gravity.BOTTOM, true);
//            mRlView.setMinimumHeight(height);
//            root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
            mRlView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter = new SelectionOneDialogAdapter();
            mAdapter.setShowCheckDefault(isShowCheckDefault);
            mRlView.setAdapter(mAdapter);
            mAdapter.setData(list);
            mAdapter.setCheckIndex(mCheckedPosition);
            mRlView.scrollToPosition(mCheckedPosition);
            mAdapter.setOnItemClickListener(new SelectionOneDialogAdapter.MyOnClickListener() {
                @Override
                public void onClick(int position) {
                    mAdapter.setCheckIndex(position);
                    mCheckedPosition = position;
                    if (mListener != null) {
                        mListener.onSelect(mCheckedPosition);
                    }
                }
            });
        }
        setCancelable(true);
        return root;
    }

    /**
     * 获取界面元素, 设置点击事件
     */
    public void initView(View root) {
        mLlSelectionWrapper = root.findViewById(R.id.llSelectionWrapper);
        mTvSelTitle = root.findViewById(R.id.tvSelTitle);
        mRlView = root.findViewById(R.id.rlView);

        tvCancel = root.findViewById(R.id.tvCancel);
        tvSure = root.findViewById(R.id.tvSure);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.dismiss();
                }
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    if (mCheckedPosition == -1) {
                        // dismiss();
                        mListener.onSure(mCheckedPosition, null);
                        return;
                    }
                    mListener.onSure(mCheckedPosition, list.get(mCheckedPosition));
                }
            }
        });
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null)
            mListener.dismiss();
    }

    public static SelectionOneDialog newInstance(ArrayList<SelectionData> list, String title, int checkedPosition, OnViewClickListener clickListener) {
        SelectionOneDialog errorDialog = new SelectionOneDialog();
        Bundle _bundle = new Bundle();
        _bundle.putParcelableArrayList(INTENT_KEY_SELECTIONDATA, list);
        _bundle.putString(INTENT_KEY_TITLE, title);
        _bundle.putInt(INTENT_KEY_CHECK_POSITION, checkedPosition);
        errorDialog.setArguments(_bundle);
        errorDialog.setOnGotoClickListener(clickListener);
        return errorDialog;
    }

    public static SelectionOneDialog newInstance(ArrayList<SelectionData> list, String title, int checkedPosition, boolean isShowCheckDefault, OnViewClickListener clickListener) {
        SelectionOneDialog errorDialog = new SelectionOneDialog();
        Bundle _bundle = new Bundle();
        _bundle.putParcelableArrayList(INTENT_KEY_SELECTIONDATA, list);
        _bundle.putString(INTENT_KEY_TITLE, title);
        _bundle.putInt(INTENT_KEY_CHECK_POSITION, checkedPosition);
        _bundle.putBoolean(INTENT_KEY_SHOW_CHECK_DEFAULT, isShowCheckDefault);
        errorDialog.setArguments(_bundle);
        errorDialog.setOnGotoClickListener(clickListener);
        return errorDialog;
    }

    public static SelectionOneDialog newInstance(ArrayList<SelectionData> list, String title, int checkedPosition, int height, OnViewClickListener clickListener) {
        SelectionOneDialog errorDialog = new SelectionOneDialog();
        Bundle _bundle = new Bundle();
        _bundle.putParcelableArrayList(INTENT_KEY_SELECTIONDATA, list);
        _bundle.putString(INTENT_KEY_TITLE, title);
        _bundle.putInt(INTENT_KEY_CHECK_POSITION, checkedPosition);
        _bundle.putInt(INTENT_KEY_DIALOG_HEIGHT, height);
        errorDialog.setArguments(_bundle);
        errorDialog.setOnGotoClickListener(clickListener);
        return errorDialog;
    }


    public static SelectionOneDialog newInstance(ArrayList<SelectionData> list, String title, int checkedPosition, int height, boolean isShowCheckDefault, OnViewClickListener clickListener) {
        SelectionOneDialog errorDialog = new SelectionOneDialog();
        Bundle _bundle = new Bundle();
        _bundle.putParcelableArrayList(INTENT_KEY_SELECTIONDATA, list);
        _bundle.putString(INTENT_KEY_TITLE, title);
        _bundle.putInt(INTENT_KEY_CHECK_POSITION, checkedPosition);
        _bundle.putInt(INTENT_KEY_DIALOG_HEIGHT, height);
        _bundle.putBoolean(INTENT_KEY_SHOW_CHECK_DEFAULT, isShowCheckDefault);
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
    private SelectionOneDialog setOnGotoClickListener(OnViewClickListener listener) {
        mListener = listener;
        return this;
    }


    @Override
    public void onDestroyView() {
        mListener = null;
        super.onDestroyView();
    }


}
