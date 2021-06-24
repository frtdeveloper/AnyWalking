package com.lenwotion.travel.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.MainActivity;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.BaseResponseBean;
import com.lenwotion.travel.interfaces.IAccountService;
import com.lenwotion.travel.utils.CommonUtil;
import com.lenwotion.travel.utils.ToastUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 注册账号或找回密码
 * Created by fq on 2017/8/16.
 */

public class RegisterActivity extends ValidateActivity {

    /**
     * 账号
     */
    private EditText mPhoneNumEt;
    /**
     * 密码
     */
    private EditText mPasswordEt;
    /**
     * 验证码
     */
    private EditText mVerifyCodeEt;
    /**
     * 功能type,1注册，2找回密码
     */
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.app_name));
        setContentView(R.layout.activity_register);
        initData();
        initView();
        initResultView();
    }

    private void initData() {
        mContext = this;
        mType = getIntent().getIntExtra("type", 1);
    }

    private void initView() {
        mPhoneNumEt = findViewById(R.id.et_phone_num);
        mVerifyCodeEt = findViewById(R.id.et_verify_code);
        mPasswordEt = findViewById(R.id.et_password);
        findViewById(R.id.bt_complete).setOnClickListener(this);
    }

    private void initResultView() {
        // 已进入内，修改密码，默认设定登陆密码
        if (TextUtils.isEmpty(UserInfoCacheUtil.getUserPhoneNum(mContext))) {
            return;
        }
        mPhoneNumEt.setText(UserInfoCacheUtil.getUserPhoneNum(mContext));
        mPhoneNumEt.setFocusable(false);
    }

    /**
     * 获取验证码按钮ID
     */
    @Override
    protected int getValidateCodeButtonId() {
        return R.id.bt_get_verify_code;
    }

    /**
     * 手机号码
     */
    @Override
    protected String getPhoneNumber() {
        return mPhoneNumEt.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.bt_complete) {
            checkupComplete();
        }
    }

    /**
     * 检查输入信息
     */
    private void checkupComplete() {
        String phone = mPhoneNumEt.getText().toString().trim();
        String password = mPasswordEt.getText().toString().trim();
        String verifyCode = mVerifyCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(mContext, getString(R.string.please_input_phone_num));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(mContext, getString(R.string.please_input_password));
            return;
        }
        // 检查密码
        if (password.length() < 6) {
            ToastUtil.showToast(mContext, getString(R.string.please_enter_at_least_6_password));
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtil.showToast(mContext, getString(R.string.please_input_verify_code));
            return;
        }
        password = CommonUtil.getMD5Encode(password);
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(mContext, getString(R.string.error_password));
            return;
        }
        //getBaseProgressDialog().show();
        requestComplete(phone, password, verifyCode);
    }

    /**
     * 完成注册账号或找回密码
     */
    private void requestComplete(final String phoneNum, String password, String identifyingCode) {
        IAccountService service = AnyWalkingApplication.getInstance().getRetrofit().create(IAccountService.class);
        Call<BaseResponseBean> call = service.register(phoneNum, password, identifyingCode, mType);
        call.enqueue(new Callback<BaseResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponseBean> call, @NonNull Response<BaseResponseBean> response) {
                //getBaseProgressDialog().dismiss();
                if (response.isSuccessful()) {
                    BaseResponseBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            registerSuccess(bean, phoneNum);
                        } else {
                            // 注册失败
                            registerFail(bean.getMsg());
                        }
                    } else {
                        // http 错误
                        registerFail(getString(R.string.http_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponseBean> call, @NonNull Throwable throwable) {
                // 超时
                registerFail(getString(R.string.http_error));
            }
        });
    }

    /**
     * 注册成功
     */
    private void registerSuccess(BaseResponseBean bean, String phoneNum) {
        ToastUtil.showToast(mContext, getString(R.string.success));
        //原来没有token，是注册登录，有token，是修改密码
        if (TextUtils.isEmpty(UserInfoCacheUtil.getUserToken(mContext))) {
            String userToken = (String) bean.getData();
            UserInfoCacheUtil.saveUserToken(mContext, userToken);
            UserInfoCacheUtil.saveUserPhoneNum(mContext, phoneNum);
            startActivity(new Intent(mContext, MainActivity.class));
        }
        finish();
    }

    /**
     * 注册失败
     */
    private void registerFail(String errMsg) {
        //getBaseProgressDialog().dismiss();
        ToastUtil.showToast(mContext, getString(R.string.fail) + ":" + errMsg);
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(UserInfoCacheUtil.getUserToken(mContext))) {
            startActivity(new Intent(mContext, LoginActivity.class));
        }
        super.onBackPressed();
    }

}
