package com.lenwotion.travel.activity.login;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.BaseResponseBean;
import com.lenwotion.travel.interfaces.IAccountService;
import com.lenwotion.travel.utils.CommonUtil;
import com.lenwotion.travel.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fq on 2017/8/16.
 * 主要是获取验证码的界面
 */

public abstract class ValidateActivity extends BaseActivity {

    private static long mLastGetValidateCodeTime = -1;
    private Button mValidateCodeBt;
    private int mMaxWaitSeconds = 60;

    protected abstract int getValidateCodeButtonId();

    protected abstract String getPhoneNumber();

    @Override
    protected void onStart() {
        super.onStart();
        if (mValidateCodeBt == null) {
            mValidateCodeBt = findViewById(getValidateCodeButtonId());
            mValidateCodeBt.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == getValidateCodeButton().getId()) {
            getValidateCode();
        }
    }

    private Button getValidateCodeButton() {
        return mValidateCodeBt;
    }

    /**
     * 验证手机号码是否有效
     */
    private boolean inputStatusIsCorrect(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNum.length() != 11) {
            Toast.makeText(this, "请输入11位手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phoneNum.startsWith("1") || !CommonUtil.isPhoneNum(phoneNum)) {
            Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 请求验证码
     */
    private void getValidateCode() {
        String phoneNum = getPhoneNumber();
        if (!inputStatusIsCorrect(phoneNum))
            return;
        if (mLastGetValidateCodeTime != -1 && ((System.currentTimeMillis() / 1000) - mLastGetValidateCodeTime) <= 60) {
            ToastUtil.showToast(mContext, "请求验证码过于频繁，请稍后再试");
            return;
        }
        // 请求接口
        IAccountService service = AnyWalkingApplication.getInstance().getRetrofit().create(IAccountService.class);
        Call<BaseResponseBean> call = service.fetchPhoneVerifyCode(phoneNum);
        call.enqueue(new Callback<BaseResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponseBean> call, @NonNull Response<BaseResponseBean> response) {
                if (response.isSuccessful()) {
                    BaseResponseBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            getValidateCodeButton().setEnabled(false);
                            startCountDown();
                            mLastGetValidateCodeTime = System.currentTimeMillis() / 1000;
                            ToastUtil.showToast(mContext, getString(R.string.success));
                        } else {
                            ToastUtil.showToast(mContext, getString(R.string.fail) + ":" + bean.getMsg());
                        }
                    } else {
                        ToastUtil.showToast(mContext, getString(R.string.http_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponseBean> call, @NonNull Throwable throwable) {
                ToastUtil.showToast(mContext, getString(R.string.http_error));
            }
        });
    }

    /**
     * 倒计时
     */
    private void startCountDown() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMaxWaitSeconds == 0) {
                    getValidateCodeButton().setText("重新获取");
                    getValidateCodeButton().setEnabled(true);
                    mMaxWaitSeconds = 60;
                } else {
                    getValidateCodeButton().setText(String.format(getResources().getString(R.string.get_verify_second), mMaxWaitSeconds));
                    if (!isFinishing()) {
                        startCountDown();
                    }
                }
                mMaxWaitSeconds--;
            }
        }, 1000);
    }

}
