package com.lenwotion.travel.activity.me;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.BaseResponseBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.interfaces.IGeneralService;
import com.lenwotion.travel.utils.ToastUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestionActivity extends BaseActivity {

    private EditText mAdviceInputEt;
    private Button mCommitBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.suggestion));
        setContentView(R.layout.activity_suggestion);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
    }

    private void initView() {
        mAdviceInputEt = findViewById(R.id.et_suggestion_input);
        mCommitBt = findViewById(R.id.bt_suggest_commit);
    }

    private void initListener() {
        mCommitBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_suggest_commit:
                commit();
                break;
        }
    }

    private void commit() {
        uploadAdvice();
    }

    private void uploadAdvice() {
        String userToken = UserInfoCacheUtil.getUserToken(mContext);
        String content = mAdviceInputEt.getText().toString().trim();
        if (TextUtils.isEmpty(userToken)) {
            ToastUtil.showToast(mContext, getString(R.string.user_info_error));
            return;
        }
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(mContext, getString(R.string.input_suggestion));
            return;
        }
        if (content.length() > 256) {
            ToastUtil.showToast(mContext, getString(R.string.input_too_much));
            return;
        }
        //getBaseProgressDialog().show();
        IGeneralService generalService = AnyWalkingApplication.getInstance().getRetrofit().create(IGeneralService.class);
        Call<BaseResponseBean> call = generalService.sendAdviceInfo(userToken, content);
        call.enqueue(new Callback<BaseResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponseBean> call, @NonNull Response<BaseResponseBean> response) {
                if (response.isSuccessful()) {
                    BaseResponseBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            submitSucceed();
                        } else {
                            // 获取服务器信息失败
                            submitFail(getString(R.string.http_error) + bean.getMsg());
                        }
                    } else {
                        // http 错误
                        submitFail(getString(R.string.http_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponseBean> call, @NonNull Throwable throwable) {
                // 超时
                submitFail(getString(R.string.http_error));
            }
        });
    }

    /**
     * 提交成功
     */
    private void submitSucceed() {
        //getBaseProgressDialog().dismiss();
        ToastUtil.showToast(mContext, getString(R.string.success));
        Log.v(GlobalConstants.LOG_TAG, "提交建议成功");
    }

    /**
     * 提交失败
     */
    private void submitFail(String error) {
        //getBaseProgressDialog().dismiss();
        ToastUtil.showToast(mContext, getString(R.string.fail) + error);
        Log.v(GlobalConstants.LOG_TAG, "提交建议失败：" + error);
    }

}
