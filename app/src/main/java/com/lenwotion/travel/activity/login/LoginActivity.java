package com.lenwotion.travel.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
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
 * 登录界面
 * Created by fq on 2017/8/16
 */
public class LoginActivity extends BaseActivity {

    /**
     * 账号
     */
    private EditText mPhoneNumEt;
    /**
     * 密码
     */
    private EditText mPasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDisplayHomeAsUpNotEnabled();
        setActionBarTitle(getString(R.string.login));
        setContentView(R.layout.activity_login);
        initData();
        initView();
    }

    private void initData() {
        mContext = this;
    }

    private void initView() {
        mPhoneNumEt = findViewById(R.id.et_login_phone_num);
        mPasswordEt = findViewById(R.id.et_login_password);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.bt_register).setOnClickListener(this);
        findViewById(R.id.bt_reset_password).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, RegisterActivity.class);
        switch (view.getId()) {

            case R.id.bt_login:
                checkupLogin();
                break;

            case R.id.bt_register:
                intent.putExtra("type", IAccountService.TYPE_REGISTER);
                startActivity(intent);
                finish();
                break;

            case R.id.bt_reset_password:
                intent.putExtra("type", IAccountService.TYPE_RESET_PASSWORD);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }

    /**
     * 检查账号和密码
     */
    private void checkupLogin() {
        String phone = mPhoneNumEt.getText().toString().trim();
        String password = mPasswordEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(mContext, getString(R.string.please_input_phone_num));
            return;
        }
        if (phone.length() != 11) {
            ToastUtil.showToast(mContext, getString(R.string.please_input_phone_num));
            return;
        }
        if (!phone.startsWith("1") || !CommonUtil.isPhoneNum(phone)) {
            ToastUtil.showToast(mContext, getString(R.string.please_input_phone_num));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(mContext, getString(R.string.please_input_password));
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            ToastUtil.showToast(mContext, getString(R.string.error_password));
            return;
        }
        password = CommonUtil.getMD5Encode(password);
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(mContext, getString(R.string.error_password));
            return;
        }
        //getBaseProgressDialog().show();
        requestLogin(phone, password);
    }

    /**
     * 请求登录
     */
    private void requestLogin(final String phoneNum, String password) {
        IAccountService loginService = AnyWalkingApplication.getInstance().getRetrofit().create(IAccountService.class);
        Call<BaseResponseBean> call = loginService.login(phoneNum, password);
        call.enqueue(new Callback<BaseResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponseBean> call, @NonNull Response<BaseResponseBean> response) {
                //getBaseProgressDialog().dismiss();
                if (response.isSuccessful()) {
                    BaseResponseBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            loginSuccess(bean, phoneNum);
                        } else {
                            // 获取服务器信息失败
                            loginFail(bean.getMsg());
                        }
                    } else {
                        // http 错误
                        loginFail(getString(R.string.http_error) + response.code());
                    }
                } else {
                    loginFail(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponseBean> call, @NonNull Throwable throwable) {
                // 超时
                loginFail(getString(R.string.http_error));
            }
        });
    }

    /**
     * 保存用户信息
     */
    public void loginSuccess(BaseResponseBean bean, String phoneNum) {
        String userToken = (String) bean.getData();
        UserInfoCacheUtil.saveUserToken(mContext, userToken);
        UserInfoCacheUtil.saveUserPhoneNum(mContext, phoneNum);
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    /**
     * 登录失败提示
     */
    public void loginFail(String errorMessage) {
        //getBaseProgressDialog().dismiss();
        ToastUtil.showToast(mContext, getString(R.string.fail) + ":" + errorMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
