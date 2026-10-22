package com.huasu.bess4c48.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.penfeizhou.animation.apng.APNGDrawable;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.utils.ToolUtil;


public class BaseDialogFragment extends DialogFragment/* implements View.OnTouchListener*/ {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.BasicDialog);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       /* if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {

                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        ToolUtil.fullScreen(window);
                    }
                });
            }
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable());
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    /**
     * 设置view宽度、高度以及显示位置
     *
     * @param viewWidth  view宽度
     * @param viewHeight view高度
     * @param gravity    view显示位置
     */
    public void setViewAttributes(int viewWidth, int viewHeight, int gravity, boolean isHiddenNavigation) {
        Window window = this.getDialog().getWindow();
        // window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();

        lp.width = viewWidth == WindowManager.LayoutParams.MATCH_PARENT ? WindowManager.LayoutParams.MATCH_PARENT : ToolUtil.dp2px(viewWidth);
        // lp.width = ToolUtil.dp2px(viewWidth);
        lp.width = viewWidth;
        lp.height = viewHeight;
        //设置dialog的位置在底部
        lp.gravity = gravity;
        window.setAttributes(lp);
        if (isHiddenNavigation) {
            //隐藏虚拟按键，并且全屏
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // lower api
                View v = window.getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //for new api versions.
                View decorView = window.getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.INVISIBLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }


    /**
     * 设置view宽度、高度以及显示位置
     *
     * @param viewWidth  view宽度
     * @param gravity    view显示位置
     */
    public void setViewAttributes(int viewWidth, int gravity, boolean isHiddenNavigation) {
        Window window = this.getDialog().getWindow();
        // window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();

        // lp.width = ToolUtil.dp2px(viewWidth);
        lp.width = viewWidth;
        //设置dialog的位置在底部
        lp.gravity = gravity;
        window.setAttributes(lp);
        if (isHiddenNavigation) {
            //隐藏虚拟按键，并且全屏
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // lower api
                View v = window.getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //for new api versions.
                View decorView = window.getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.INVISIBLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        boolean isShow = this.getShowsDialog();
        this.setShowsDialog(false);
        super.onActivityCreated(savedInstanceState);
        this.setShowsDialog(isShow);

        View view = getView();
        if (view != null) {
           /* if (view.getParent() != null) {
                throw new IllegalStateException(
                        "DialogFragment can not be attached to a container view");
            }*/
            this.getDialog().setContentView(view);
        }
        final Activity activity = getActivity();
        if (activity != null) {
            this.getDialog().setOwnerActivity(activity);
        }
        this.getDialog().setCancelable(false);
        //  this.getDialog().getWindow().getDecorView().setOnTouchListener(this);
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        if (savedInstanceState != null) {
            Bundle dialogState = savedInstanceState.getBundle("android:savedDialogState");
            if (dialogState != null) {
                this.getDialog().onRestoreInstanceState(dialogState);
            }
        }
    }

    /* @Override
     public boolean onTouch(View v, MotionEvent event) {
         //触摸外部弹窗
         if (isOutOfBounds(getContext(), event)) {
             dismiss();
             return true;
         }
         return false;
     }
 */
    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getDialog().getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }

    @Override
    public void show(FragmentManager manager, String tag) {
//        super.show(manager, tag);
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }


    public View getLoadingView() {
        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
        ImageView ivLoading = loadingView.findViewById(R.id.ivLoading);
        ivLoading.setImageDrawable(APNGDrawable.fromAsset(getContext(), "loading.png"));
        return loadingView;
    }

    /**
     * dialog 需要全屏的时候用，和clearFocusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     *
     * @param window
     */
    private void focusNotAle(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * dialog 需要全屏的时候用，focusNotAle() 成对出现
     * 在show 前调用  focusNotAle   show后调用clearFocusNotAle
     *
     * @param window
     */
    private void clearFocusNotAle(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * 隐藏虚拟栏 ，显示的时候再隐藏掉
     *
     * @param window
     */
    public void hideNavigationBar(final Window window) {
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        int flag = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.getDecorView().setSystemUiVisibility(flag);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                window.getDecorView().setSystemUiVisibility(uiOptions);
            }
        });
    }


}

