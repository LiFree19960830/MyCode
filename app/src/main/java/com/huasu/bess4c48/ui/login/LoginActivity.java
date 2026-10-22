package com.huasu.bess4c48.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.R;
import com.huasu.bess4c48.base.BaseActivity;
import com.huasu.bess4c48.bean.UserInfo;
import com.huasu.bess4c48.ui.MainActivity;
import com.huasu.bess4c48.utils.LanguageUtil;

/**
 * 登录页
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    EditText mEditTextLoginName;
    EditText mEditTextLoginPassword;
    ImageView mIvPasswordSwitch;
    TextView mTvResult;
    Button mBtnSubmit;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_login;
    }

    @RequiresApi(api = 30)
    protected void initView() {
        requestPermissions();
        mEditTextLoginName = findViewById(R.id.editTextLoginName);
        mEditTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        mIvPasswordSwitch = findViewById(R.id.ivPasswordSwitch);
        mTvResult = findViewById(R.id.tvResult);
        mBtnSubmit = findViewById(R.id.btnSubmit);
/*        mEditTextLoginPassword.setText("301157");
        mEditTextLoginName.setText("admin");*/
        mIvPasswordSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPassword();
            }
        });
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        mIvPasswordSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPassword();
            }
        });
    }

    @Override
    protected boolean isTitleBar() {
        return false;
    }

    @Override
    protected String getTitleMsg() {
        return null;
    }

    // Android6.0系统申请读写外部存储的运行时权限（该方法在需要操作外部存储时调用，或在相关初始化操作中调用）
    @RequiresApi(api = 30)
    private void requestPermissions() {
        // 如果未获得外部存储读写权限，则申请
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (!checkPermission()) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20);
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public static void launchActivity() {
        Intent intent = new Intent(MyApplication.mApplication, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.mApplication.startActivity(intent);
    }


    /* 切换密码框显示隐藏状态 */
    private void switchPassword() {
        int inputType = mEditTextLoginPassword.getInputType();
        if (inputType == 129) {
            mEditTextLoginPassword.setInputType(145);
            mIvPasswordSwitch.setBackgroundResource(R.mipmap.ic_eye_open);
            mEditTextLoginPassword.setSelection(mEditTextLoginPassword.getText().length());
        } else {
            mEditTextLoginPassword.setInputType(129);
            mIvPasswordSwitch.setBackgroundResource(R.mipmap.ic_eye_close);
            mEditTextLoginPassword.setSelection(mEditTextLoginPassword.getText().length());
        }
    }

    /* 登录按钮点击事件 */
    private void submit() {
        if (TextUtils.isEmpty(mEditTextLoginName.getText())) {
            mTvResult.setText(getString(R.string.nameIsNull));
            return;
        }
        if (TextUtils.isEmpty(mEditTextLoginPassword.getText())) {
            mTvResult.setText(getString(R.string.PassIsNull));
            return;
        }
        String loginName = mEditTextLoginName.getText().toString();
        String loginPassword = mEditTextLoginPassword.getText().toString();
        mTvResult.setText("");
        if (loginName.equals("admin") && loginPassword.equals("301157")){
            UserInfo userInfo = new UserInfo();
            userInfo.loginName = "admin";
            userInfo.privilegeLevel = 1;
            MyApplication.getInstance().userInfo = userInfo;
            MainActivity.launchActivity();
             finish();
        } else if (loginName.equals("user") && loginPassword.equals("123456")){
            UserInfo userInfo = new UserInfo();
            userInfo.loginName = "user";
            userInfo.privilegeLevel = 0;
            MyApplication.getInstance().userInfo = userInfo;
            MainActivity.launchActivity();
            finish();
        } else
            mTvResult.setText(R.string.login_fail);

    }

}
