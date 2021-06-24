package com.lenwotion.travel.activity.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.enums.NaviType;
import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.utils.DataSaveUtil;
import com.lenwotion.travel.utils.ToastUtil;

/**
 * 导航类型选择页面
 * Created by john on 2017/11/27
 */
public class NavigationTypeActivity extends BaseActivity {

    /**
     * 终点GPS
     */
    private LatLng mLatLng;
    /**
     * 目的地名称
     */
    private String mPositionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.navigation_type));
        setContentView(R.layout.activity_navigation_type);
        initData();
        initView();
    }

    private void initData() {
        mContext = this;
        mLatLng = getIntent().getParcelableExtra("latLng");
        if (mLatLng == null) {
            ToastUtil.showToast(mContext, getString(R.string.system_error));
            finish();
            return;
        }
        mPositionTitle = getIntent().getStringExtra("positionTitle");
    }

    private void initView() {
        Button emulatorNavigationBt = findViewById(R.id.bt_emulator_navigation);
        Button navigationPlanningBt = findViewById(R.id.bt_navigation_planning);
        Button gpsNavigationBt = findViewById(R.id.bt_gps_navigation);
        emulatorNavigationBt.setOnClickListener(this);
        navigationPlanningBt.setOnClickListener(this);
        gpsNavigationBt.setOnClickListener(this);
        TextView mPositionTv = findViewById(R.id.tv_position);
        if (!TextUtils.isEmpty(mPositionTitle)) {
            String info = getString(R.string.will_navigation_to) + mPositionTitle + "\n" +
                    getString(R.string.choose_navigation_type);
            mPositionTv.setText(info);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            // 模拟导航
            case R.id.bt_emulator_navigation:
                if (mLatLng == null) {
                    ToastUtil.showToast(mContext, getString(R.string.system_error));
                    return;
                }
                intent = new Intent(mContext, WalkRouteCalculateActivity.class);
                intent.putExtra("naviType", NaviType.EMULATOR);
                intent.putExtra("latLng", mLatLng);
                startActivity(intent);
                break;

            // 模拟导航列表
            case R.id.bt_navigation_planning:
                if (DataSaveUtil.getNavigationPlanningList().isEmpty()) {
                    ToastUtil.showToast(mContext, getString(R.string.please_use_simulation_navigation));
                    return;
                }
                startActivity(new Intent(mContext, NavigationPlanningActivity.class));
                break;

            // 实时导航
            case R.id.bt_gps_navigation:
                if (mLatLng == null) {
                    ToastUtil.showToast(mContext, getString(R.string.system_error));
                    return;
                }
                intent = new Intent(mContext, WalkRouteCalculateActivity.class);
                intent.putExtra("naviType", NaviType.GPS);
                intent.putExtra("latLng", mLatLng);
                startActivity(intent);
                break;

            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        DataSaveUtil.clearNavigationPlanningList();
        super.onDestroy();
    }

}
