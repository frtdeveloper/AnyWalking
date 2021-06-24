package com.lenwotion.travel.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.utils.SharedPreferencesUtil;

public class TestGPSActivity extends BaseActivity {

    private Context mContext;

    private EditText mLatEt;
    private EditText mLngEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gps);
        initData();
        initView();
    }

    private void initData() {
        mContext = this;
    }

    private void initView() {
        mLatEt = findViewById(R.id.et_test_gps_lat);
        mLngEt = findViewById(R.id.et_test_gps_lng);
        mLatEt.setText(GlobalVariables.TEST_LAT);
        mLngEt.setText(GlobalVariables.TEST_LNG);
        findViewById(R.id.bt_set_test_gps_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.TEST_LAT = mLatEt.getText().toString();
                GlobalVariables.TEST_LNG = mLngEt.getText().toString();
                SharedPreferencesUtil.setString(mContext, "TestLat", GlobalVariables.TEST_LAT);
                SharedPreferencesUtil.setString(mContext, "TestLng", GlobalVariables.TEST_LNG);
                finish();
            }
        });
    }

}
