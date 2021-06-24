package com.lenwotion.travel.activity.me;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;

public class InfoActivity extends BaseActivity {

    private TextView mInfoTv;
    private String mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkType();
        setContentView(R.layout.activity_info);
        initData();
        initView();
        initResultView();
    }

    private void checkType() {
        String type = getIntent().getStringExtra(AboutActivity.ABOUT_INFO_TYPE_KEY);
        switch (type) {
            case AboutActivity.ABOUT_INFO_TYPE_COMPANY_CITY:
                setActionBarTitle(getString(R.string.about_company_city));
                mInfo = getString(R.string.about_company_city_info);
                break;

            case AboutActivity.ABOUT_INFO_TYPE_COMPANY:
                setActionBarTitle(getString(R.string.about_company));
                mInfo = getString(R.string.about_company_info);
                break;

            case AboutActivity.ABOUT_INFO_TYPE_APP:
                setActionBarTitle(getString(R.string.about_app));
                mInfo = getString(R.string.about_app_info);
                break;

            case AboutActivity.ABOUT_INFO_TYPE_AGREEMENT:
                setActionBarTitle(getString(R.string.privacy_agreement));
                mInfo = getString(R.string.agreement_text);
                break;
        }
    }

    private void initData() {
        mContext = this;
    }

    private void initView() {
        mInfoTv = findViewById(R.id.tv_me_info);
        mInfoTv.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initResultView() {
        mInfoTv.setText(mInfo);
    }

}
