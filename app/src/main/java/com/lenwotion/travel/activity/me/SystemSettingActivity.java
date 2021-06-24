package com.lenwotion.travel.activity.me;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.utils.SharedPreferencesUtil;

public class SystemSettingActivity extends BaseActivity {

    private Switch mFactorySw;
    private Switch mShowSw;
    private Switch mGpsSw;
    private Switch mLocalSw;

    private boolean mIsFactory;
    private boolean mIsShow;
    private boolean mIsGps;
    private boolean mIsLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.setting));
        setContentView(R.layout.activity_system_info);
        initData();
        initView();
        initResultView();
        initListener();
    }

    private void initData() {
        mContext = this;
        getSystemStatus();
    }

    private void getSystemStatus() {
        mIsFactory = SharedPreferencesUtil.getBoolean(mContext, "FactoryModel");
        mIsShow = SharedPreferencesUtil.getBoolean(mContext, "ShowModel");
        mIsGps = SharedPreferencesUtil.getBoolean(mContext, "GpsModel");
        mIsLocal = SharedPreferencesUtil.getBoolean(mContext, "LocalModel");
    }

    private void initView() {
        mFactorySw = findViewById(R.id.sw_system_factory);
        mShowSw = findViewById(R.id.sw_system_show);
        mGpsSw = findViewById(R.id.sw_system_gps);
        mLocalSw = findViewById(R.id.sw_system_local);
    }

    private void initResultView() {
        mFactorySw.setChecked(mIsFactory);
        mShowSw.setChecked(mIsShow);
        mGpsSw.setChecked(mIsGps);
        mLocalSw.setChecked(mIsLocal);
    }

    private void initListener() {
        mFactorySw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.setBoolean(mContext, "FactoryModel", isChecked);
            }
        });
        mShowSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.setBoolean(mContext, "ShowModel", isChecked);
            }
        });
        mGpsSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.setBoolean(mContext, "GpsModel", isChecked);
            }
        });
        mLocalSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.setBoolean(mContext, "LocalModel", isChecked);
            }
        });
    }

}
